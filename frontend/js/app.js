// ===========================================
// ГЛОБАЛЬНЫЕ ПЕРЕМЕННЫЕ
// ===========================================
let currentUser = null;
let jwtToken = null;
let taskModal = null;

// ===========================================
// API URL - теперь все через Gateway!
// ===========================================
const API = {
    GATEWAY: 'http://localhost:8080',  // ← единая точка входа
    USERS: '/api/users',
    TASKS: '/api/tasks',
    AUTH: '/api/auth'
};

// ===========================================
// ИНИЦИАЛИЗАЦИЯ
// ===========================================
$(document).ready(function() {
    console.log('🚀 Приложение запущено! (микросервисная версия)');

    taskModal = new bootstrap.Modal(document.getElementById('taskModal'));
    checkSavedAuth();
    bindEvents();
});

// ===========================================
// ПРОВЕРКА СОХРАНЕННОЙ АВТОРИЗАЦИИ
// ===========================================
function checkSavedAuth() {
    const savedToken = localStorage.getItem('jwt_token');
    const savedUser = localStorage.getItem('current_user');

    if (savedToken && savedUser) {
        jwtToken = savedToken;
        currentUser = JSON.parse(savedUser);
        updateUIForAuthUser();
        loadTasks();
    }
}

// ===========================================
// ПРИВЯЗКА СОБЫТИЙ
// ===========================================
function bindEvents() {
    console.log('🔧 Привязываем события...');

    $('#registerBtn').on('click', registerUser);
    $('#loginBtn').on('click', loginUser);
    $('#logoutBtn').on('click', logoutUser);

    $('#addTaskBtn').on('click', function(e) {
        e.preventDefault();
        console.log('👆 Добавление задачи');
        addTask();
    });

    $('#taskTitle').on('keypress', function(e) {
        if (e.which === 13) addTask();
    });

    $('#deleteTaskBtn').on('click', deleteTask);

    console.log('✅ События привязаны');
}

// ===========================================
// AJAX ЗАГОЛОВКИ
// ===========================================
function getAuthHeaders() {
    const headers = {
        'Content-Type': 'application/json'
    };

    if (jwtToken) {
        headers['Authorization'] = `Bearer ${jwtToken}`;
        console.log('🔑 Токен:', jwtToken.substring(0, 20) + '...');
    }

    return headers;
}

// ===========================================
// РЕГИСТРАЦИЯ
// ===========================================
function registerUser() {
    const name = $('#regName').val();
    const email = $('#regEmail').val();
    const password = $('#regPassword').val();
    const passwordRepeat = $('#regPasswordRepeat').val();

    $('#registerError').hide();

    if (!email || !name || !password || !passwordRepeat) {
        showRegisterError('Все поля обязательны');
        return;
    }

    if (password !== passwordRepeat) {
        showRegisterError('Пароли не совпадают');
        return;
    }

    $.ajax({
        url: `${API.GATEWAY}${API.AUTH}/register`,  // ← через Gateway
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({ name, email, password }),
        success: function(response) {
            console.log('✅ Регистрация:', response);

            jwtToken = response.token;
            currentUser = {
                email: response.email,
                name: response.name,
                role: response.role
            };

            localStorage.setItem('jwt_token', jwtToken);
            localStorage.setItem('current_user', JSON.stringify(currentUser));

            $('#registerModal').modal('hide');
            $('#registerForm')[0].reset();
            $('#registerError').hide();

            updateUIForAuthUser();
            loadTasks();
        },
        error: function(xhr) {
            console.error('❌ Ошибка регистрации:', xhr);
            showRegisterError(xhr.responseJSON?.message || 'Ошибка регистрации');
        }
    });
}

function showRegisterError(message) {
    $('#registerError').html(message).show();
}

// ===========================================
// ВХОД
// ===========================================
function loginUser() {
    const email = $('#loginEmail').val();
    const password = $('#loginPassword').val();

    $('#loginError').hide();

    if (!email || !password) {
        showLoginError('Введите email и пароль');
        return;
    }

    $.ajax({
        url: `${API.GATEWAY}${API.AUTH}/login`,  // ← через Gateway
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({ email, password }),
        success: function(response) {
            console.log('✅ Вход выполнен:', response);

            jwtToken = response.token;
            currentUser = {
                email: response.email,
                name: response.name,
                role: response.role
            };

            localStorage.setItem('jwt_token', jwtToken);
            localStorage.setItem('current_user', JSON.stringify(currentUser));

            $('#loginModal').modal('hide');
            $('#loginForm')[0].reset();
            $('#loginError').hide();

            updateUIForAuthUser();
            loadTasks();
        },
        error: function(xhr) {
            console.error('❌ Ошибка входа:', xhr);
            showLoginError('Неверный email или пароль');
        }
    });
}

function showLoginError(message) {
    $('#loginError').html(message).show();
}

// ===========================================
// ВЫХОД
// ===========================================
function logoutUser() {
    localStorage.removeItem('jwt_token');
    localStorage.removeItem('current_user');
    jwtToken = null;
    currentUser = null;
    updateUIForUnauthUser();
    $('#active-tasks, #completed-tasks').empty();
}

// ===========================================
// ОБНОВЛЕНИЕ ИНТЕРФЕЙСА
// ===========================================
function updateUIForAuthUser() {
    $('#authorized-header').show();
    $('#main-content').show();
    $('#unauthorized-header').hide();

    if (currentUser) {
        $('#current-user').text(currentUser.name || currentUser.email);
    }
}

function updateUIForUnauthUser() {
    $('#unauthorized-header').show();
    $('#authorized-header').hide();
    $('#main-content').hide();
}

// ===========================================
// ЗАГРУЗКА ЗАДАЧ
// ===========================================
function loadTasks() {
    if (!currentUser) return;

    $('#active-tasks').html('<div class="loading">Загрузка...</div>');
    $('#completed-tasks').html('<div class="loading">Загрузка...</div>');

    $.ajax({
        url: `${API.GATEWAY}${API.TASKS}`,  // ← через Gateway
        method: 'GET',
        headers: getAuthHeaders(),
        success: function(tasks) {
            console.log('📋 Задачи загружены:', tasks);

            const activeTasks = tasks.filter(task => task.status !== 'COMPLETED');
            const completedTasks = tasks.filter(task => task.status === 'COMPLETED');

            renderTasks(activeTasks, 'active');
            renderTasks(completedTasks, 'completed');
        },
        error: function(xhr) {
            console.error('❌ Ошибка загрузки задач:', xhr);
            if (xhr.status === 401 || xhr.status === 403) {
                logoutUser();
                $('#loginModal').modal('show');
            }
        }
    });
}

// ===========================================
// ОТОБРАЖЕНИЕ ЗАДАЧ
// ===========================================
function renderTasks(tasks, type) {
    const container = type === 'active' ? '#active-tasks' : '#completed-tasks';

    if (tasks.length === 0) {
        $(container).html('<p class="text-muted">Нет задач</p>');
        return;
    }

    let html = '';
    tasks.forEach(task => {
        html += `
            <div class="task-item ${type === 'active' ? 'active-task' : 'completed-task'}"
                 onclick="openTaskModal('${task.id}')">
                <strong>${escapeHtml(task.name)}</strong>
                <br>
                <small>${escapeHtml(task.description || 'Нет описания')}</small>
                <br>
                <small class="text-muted">Дедлайн: ${task.deadline || 'Не указан'}</small>
            </div>
        `;
    });

    $(container).html(html);
}

function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// ===========================================
// ДОБАВЛЕНИЕ ЗАДАЧИ
// ===========================================
function addTask() {
    const title = $('#taskTitle').val().trim();
    const description = $('#taskDescription').val().trim() || 'Новая задача';
    const deadline = $('#taskDeadline').val() || new Date().toISOString().split('T')[0];

    if (!title) {
        alert('Введите название задачи');
        return;
    }

    const taskData = { name: title, description, deadline };
    console.log('📝 Создаем задачу:', taskData);

    $.ajax({
        url: `${API.GATEWAY}${API.TASKS}`,
        method: 'POST',
        headers: getAuthHeaders(),
        contentType: 'application/json',
        data: JSON.stringify(taskData),
        success: function(response) {
            console.log('✅ Задача создана:', response);
            $('#taskTitle').val('');
            $('#taskDescription').val('');
            loadTasks();
        },
        error: function(xhr) {
            console.error('❌ Ошибка:', xhr);
            alert('Ошибка: ' + (xhr.responseJSON?.message || 'Не удалось создать задачу'));
        }
    });
}

// ===========================================
// ОТКРЫТИЕ ЗАДАЧИ
// ===========================================
function openTaskModal(taskId) {
    console.log('📂 Открываем задачу:', taskId);

    $.ajax({
        url: `${API.GATEWAY}${API.TASKS}/${taskId}`,
        method: 'GET',
        headers: getAuthHeaders(),
        success: function(task) {
            console.log('📄 Данные задачи:', task);

            $('#taskId').val(task.id);
            $('#taskName').val(task.name || '');
            $('#taskDescription').val(task.description || '');
            $('#taskStatus').prop('checked', task.status === 'COMPLETED');

            taskModal.show();
        },
        error: function(xhr) {
            console.error('❌ Ошибка загрузки задачи:', xhr);
            alert('Не удалось загрузить задачу');
        }
    });
}

// ===========================================
// СОХРАНЕНИЕ ПОЛЯ ЗАДАЧИ
// ===========================================
function saveTaskField(fieldName) {
    const taskId = $('#taskId').val();
    if (!taskId) return;

    let updateData = { id: taskId };

    switch(fieldName) {
        case 'name':
            updateData.name = $('#taskName').val();
            break;
        case 'description':
            updateData.description = $('#taskDescription').val();
            break;
        case 'status':
            updateData.status = $('#taskStatus').is(':checked') ? 'COMPLETED' : 'ACTIVE';
            break;
    }

    console.log('💾 Сохраняем поле:', updateData);

    $.ajax({
        url: `${API.GATEWAY}${API.TASKS}/${taskId}`,
        method: 'PATCH',
        headers: getAuthHeaders(),
        contentType: 'application/json',
        data: JSON.stringify(updateData),
        success: function() {
            console.log(`✅ Поле ${fieldName} сохранено`);
            if (fieldName === 'status') loadTasks();
        },
        error: function(xhr) {
            console.error(`❌ Ошибка сохранения ${fieldName}:`, xhr);
            alert('Не удалось сохранить изменения');
        }
    });
}

// ===========================================
// УДАЛЕНИЕ ЗАДАЧИ
// ===========================================
function deleteTask() {
    const taskId = $('#taskId').val();
    if (!taskId) return;

    if (confirm('Удалить задачу?')) {
        $.ajax({
            url: `${API.GATEWAY}${API.TASKS}/${taskId}`,
            method: 'DELETE',
            headers: getAuthHeaders(),
            success: function() {
                console.log('🗑️ Задача удалена');
                taskModal.hide();
                loadTasks();
            },
            error: function(xhr) {
                console.error('❌ Ошибка удаления:', xhr);
                alert('Не удалось удалить задачу');
            }
        });
    }
}