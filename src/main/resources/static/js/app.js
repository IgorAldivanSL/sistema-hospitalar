const CONFIG = {
    pacientes: {
        title: 'Pacientes',
        endpoint: '/api/v1/pacientes',
        columns: ['id', 'nome', 'cpf', 'email', 'telefone'],
        schema: [
            { name: 'nome', type: 'text', label: 'Nome Completo', required: true },
            { name: 'cpf', type: 'text', label: 'CPF (Somente Números)', required: true },
            { name: 'dataNascimento', type: 'date', label: 'Data de Nascimento', required: true },
            { name: 'email', type: 'email', label: 'E-mail', required: true },
            { name: 'telefone', type: 'text', label: 'Telefone', required: true }
        ]
    },
    medicos: {
        title: 'Médicos',
        endpoint: '/api/v1/medicos',
        columns: ['id', 'nome', 'crm', 'email', 'telefone'],
        schema: [
            { name: 'nome', type: 'text', label: 'Nome Completo', required: true },
            { name: 'crm', type: 'text', label: 'CRM', required: true },
            { name: 'email', type: 'email', label: 'E-mail', required: true },
            { name: 'telefone', type: 'text', label: 'Telefone', required: true },
            { name: 'especialidadeIds', type: 'text', label: 'IDs das Especialidades (separados por vírgula)', required: false }
        ]
    },
    especialidades: {
        title: 'Especialidades',
        endpoint: '/api/v1/especialidades',
        columns: ['id', 'nome', 'descricao'],
        schema: [
            { name: 'nome', type: 'text', label: 'Nome da Especialidade', required: true },
            { name: 'descricao', type: 'textarea', label: 'Descrição', required: false }
        ]
    },
    consultas: {
        title: 'Consultas',
        endpoint: '/api/v1/consultas',
        columns: ['id', 'pacienteNome', 'medicoNome', 'dataHora', 'status'],
        schema: [
            { name: 'pacienteId', type: 'number', label: 'ID do Paciente', required: true },
            { name: 'medicoId', type: 'number', label: 'ID do Médico', required: true },
            { name: 'dataHora', type: 'datetime-local', label: 'Data e Hora', required: true },
            { name: 'status', type: 'select', label: 'Status', options: ['AGENDADA', 'REALIZADA', 'CANCELADA'], required: true }
        ]
    },
    prontuarios: {
        title: 'Prontuários',
        endpoint: '/api/v1/prontuarios',
        columns: ['id', 'pacienteNome', 'tipoSanguineo'],
        schema: [
            { name: 'pacienteId', type: 'number', label: 'ID do Paciente', required: true },
            { name: 'historico', type: 'textarea', label: 'Histórico Médico', required: true },
            { name: 'alergias', type: 'text', label: 'Alergias', required: false },
            { name: 'tipoSanguineo', type: 'select', label: 'Tipo Sanguíneo', options: ['A_POSITIVO', 'A_NEGATIVO', 'B_POSITIVO', 'B_NEGATIVO', 'AB_POSITIVO', 'AB_NEGATIVO', 'O_POSITIVO', 'O_NEGATIVO'], required: true }
        ]
    },
    apikeys: {
        title: 'API Keys',
        endpoint: '/api/v1/auth/keys',
        columns: ['id', 'cliente', 'keyValue', 'ativo'],
        schema: [
            { name: 'cliente', type: 'text', label: 'Nome do Cliente / Aplicação', required: true }
        ]
    }
};

let currentEntity = 'pacientes';
let currentPageState = 0;
let currentEditingId = null;

document.addEventListener('DOMContentLoaded', () => {
    initNavigation();
    initControls();
    loadData();
});

function initNavigation() {
    document.querySelectorAll('.nav-item').forEach(btn => {
        btn.addEventListener('click', (e) => {
            document.querySelectorAll('.nav-item').forEach(b => b.classList.remove('active'));
            e.currentTarget.classList.add('active');
            currentEntity = e.currentTarget.dataset.entity;
            document.getElementById('pageTitle').textContent = CONFIG[currentEntity].title;
            currentPageState = 0;
            loadData();
        });
    });
}

function initControls() {
    document.getElementById('btnRefresh').addEventListener('click', loadData);
    document.getElementById('btnNovo').addEventListener('click', () => openModal());
    document.getElementById('btnCloseModal').addEventListener('click', closeModal);
    document.getElementById('btnCancelModal').addEventListener('click', closeModal);
    document.getElementById('btnCloseDetails').addEventListener('click', () => document.getElementById('detailsModal').classList.add('hidden'));
    document.getElementById('btnSaveModal').addEventListener('click', saveData);
    
    document.getElementById('btnPrev').addEventListener('click', () => {
        if(currentPageState > 0) { currentPageState--; loadData(); }
    });
    document.getElementById('btnNext').addEventListener('click', () => {
        currentPageState++; loadData();
    });
    document.getElementById('pageSize').addEventListener('change', () => {
        currentPageState = 0; loadData();
    });
}

function getHeaders() {
    const headers = { 'Content-Type': 'application/json' };
    const apiKey = document.getElementById('globalApiKey').value;
    const idemKey = document.getElementById('globalIdempotency').value;
    const version = document.getElementById('globalVersion').value;
    
    if (apiKey) headers['X-API-Key'] = apiKey;
    if (idemKey) headers['X-Idempotency-Key'] = idemKey;
    if (version) headers['X-API-Version'] = version;
    
    return headers;
}

async function loadData() {
    showLoading(true);
    const conf = CONFIG[currentEntity];
    const size = document.getElementById('pageSize').value;
    const sort = 'id,desc';
    
    try {
        const response = await fetch(`${conf.endpoint}?page=${currentPageState}&size=${size}&sort=${sort}`, {
            headers: getHeaders()
        });
        
        if (!response.ok) await handleApiError(response);
        
        const data = await response.json();
        renderTable(data);
    } catch (error) {
        showToast(error.message || 'Erro ao carregar dados', 'error');
        renderTable({ _embedded: null, page: { totalElements: 0 } });
    } finally {
        showLoading(false);
    }
}

function renderTable(data) {
    const conf = CONFIG[currentEntity];
    const tableHeader = document.getElementById('tableHeader');
    const tableBody = document.getElementById('tableBody');
    
    tableHeader.innerHTML = '';
    conf.columns.forEach(col => {
        tableHeader.innerHTML += `<th>${col.toUpperCase()}</th>`;
    });
    tableHeader.innerHTML += `<th>AÇÕES</th>`;
    
    tableBody.innerHTML = '';
    
    let items = [];
    if (data._embedded) {
        const key = Object.keys(data._embedded)[0];
        items = data._embedded[key];
    } else if (Array.isArray(data)) {
        items = data; 
    }

    if (items.length === 0) {
        document.getElementById('emptyState').classList.remove('hidden');
        document.querySelector('table').classList.add('hidden');
    } else {
        document.getElementById('emptyState').classList.add('hidden');
        document.querySelector('table').classList.remove('hidden');
        
        items.forEach(item => {
            let row = `<tr>`;
            conf.columns.forEach(col => {
                row += `<td>${item[col] !== undefined && item[col] !== null ? item[col] : '-'}</td>`;
            });
            row += `
                <td class="action-cell">
                    <button class="action-btn" title="Visualizar" onclick='viewDetails(${JSON.stringify(item)})'><i class="fas fa-eye"></i></button>
                    <button class="action-btn" title="Editar" onclick='openModal(${JSON.stringify(item)})'><i class="fas fa-edit"></i></button>
                    <button class="action-btn delete" title="Excluir" onclick="deleteItem(${item.id})"><i class="fas fa-trash"></i></button>
                </td>
            `;
            row += `</tr>`;
            tableBody.innerHTML += row;
        });
    }

    const page = data.page || { number: 0, totalPages: 0, totalElements: items.length };
    document.getElementById('currentPage').textContent = page.number + 1;
    document.getElementById('totalItems').textContent = page.totalElements;
    
    document.getElementById('btnPrev').disabled = page.number === 0;
    document.getElementById('btnNext').disabled = page.number >= page.totalPages - 1 || page.totalPages === 0;
}

function openModal(item = null) {
    const conf = CONFIG[currentEntity];
    const form = document.getElementById('dynamicForm');
    form.innerHTML = '';
    currentEditingId = item ? item.id : null;
    
    document.getElementById('modalTitle').textContent = item ? `Editar ${conf.title}` : `Novo ${conf.title}`;
    
    conf.schema.forEach(field => {
        let inputHtml = '';
        const value = item ? (item[field.name] || '') : '';
        
        if (field.type === 'textarea') {
            inputHtml = `<textarea id="f_${field.name}" name="${field.name}" rows="3" ${field.required ? 'required' : ''}>${value}</textarea>`;
        } else if (field.type === 'select') {
            let opts = field.options.map(o => `<option value="${o}" ${value === o ? 'selected' : ''}>${o}</option>`).join('');
            inputHtml = `<select id="f_${field.name}" name="${field.name}" ${field.required ? 'required' : ''}>${opts}</select>`;
        } else {
            let displayVal = value;
            if(Array.isArray(value)) displayVal = value.join(',');
            
            inputHtml = `<input type="${field.type}" id="f_${field.name}" name="${field.name}" value="${displayVal}" ${field.required ? 'required' : ''}>`;
        }
        
        form.innerHTML += `
            <div class="form-group">
                <label for="f_${field.name}">${field.label}</label>
                ${inputHtml}
            </div>
        `;
    });
    
    document.getElementById('formModal').classList.remove('hidden');
}

function closeModal() {
    document.getElementById('formModal').classList.add('hidden');
}

async function saveData() {
    const conf = CONFIG[currentEntity];
    const formElement = document.getElementById('dynamicForm');
    if (!formElement.checkValidity()) {
        formElement.reportValidity();
        return;
    }
    
    const formData = new FormData(formElement);
    const payload = {};
    
    conf.schema.forEach(field => {
        let val = formData.get(field.name);
        if (field.name === 'especialidadeIds') {
            payload[field.name] = val ? val.split(',').map(n => parseInt(n.trim())) : [];
        } else if (field.type === 'number') {
            payload[field.name] = val ? parseInt(val) : null;
        } else {
            payload[field.name] = val;
        }
    });

    const method = currentEditingId ? 'PUT' : 'POST';
    const url = currentEditingId ? `${conf.endpoint}/${currentEditingId}` : conf.endpoint;
    
    try {
        const btn = document.getElementById('btnSaveModal');
        btn.disabled = true;
        btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Salvando...';
        
        const response = await fetch(url, {
            method: method,
            headers: getHeaders(),
            body: JSON.stringify(payload)
        });
        
        if (!response.ok) await handleApiError(response);
        
        showToast('Registro salvo com sucesso!', 'success');
        closeModal();
        loadData();
    } catch (error) {
        showToast(error.message, 'error');
    } finally {
        const btn = document.getElementById('btnSaveModal');
        btn.disabled = false;
        btn.innerHTML = '<i class="fas fa-save"></i> Salvar';
    }
}

async function deleteItem(id) {
    if (!confirm('Tem certeza que deseja excluir este registro?')) return;
    
    try {
        const response = await fetch(`${CONFIG[currentEntity].endpoint}/${id}`, {
            method: 'DELETE',
            headers: getHeaders()
        });
        
        if (!response.ok) await handleApiError(response);
        
        showToast('Registro excluído com sucesso', 'success');
        loadData();
    } catch (error) {
        showToast(error.message, 'error');
    }
}

function viewDetails(item) {
    document.getElementById('detailsContent').innerHTML = `<pre>${JSON.stringify(item, null, 2)}</pre>`;
    document.getElementById('detailsModal').classList.remove('hidden');
}

async function handleApiError(response) {
    let msg = `Erro HTTP ${response.status}`;
    try {
        const text = await response.text();
        if (text) {
            try {
                const errJson = JSON.parse(text);
                if (errJson.message) msg = errJson.message;
                else if (errJson.details) msg = errJson.details.join(', ');
                else msg = JSON.stringify(errJson);
            } catch(e) {
                // Se não conseguir fazer o parse, significa que é texto puro (ex: 401 Unauthorized)
                msg = text;
            }
        }
    } catch(e) {
        // Fallback em caso de erro extremo na leitura
    }
    throw new Error(msg);
}

function showToast(message, type = 'success') {
    const container = document.getElementById('toastContainer');
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    
    let icon = 'check-circle';
    if(type === 'error') icon = 'exclamation-circle';
    if(type === 'warning') icon = 'exclamation-triangle';
    
    toast.innerHTML = `<i class="fas fa-${icon}"></i> <span>${message}</span>`;
    container.appendChild(toast);
    
    setTimeout(() => {
        toast.style.animation = 'slideIn 0.3s ease-out reverse forwards';
        setTimeout(() => toast.remove(), 300);
    }, 4000);
}

function showLoading(show) {
    const loader = document.getElementById('loadingIndicator');
    if (show) loader.classList.remove('hidden');
    else loader.classList.add('hidden');
}
