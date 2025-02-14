let updateInterval;

function startUpdatingTimes() {
    updateAllTimes();
    // Update every 1 second (1000 milliseconds)
    updateInterval = setInterval(updateAllTimes, 1000);
}

function updateAllTimes() {
    document.querySelectorAll('p[data-offset]').forEach(element => {
        const offset = element.getAttribute('data-offset');
        updateSingleTime(element, offset);
    });
}

function updateSingleTime(element, utcOffset) {
    let offset = parseFloat(utcOffset);

    // Check if the offset is valid
    if (isNaN(offset)) {
        element.textContent = `${element.textContent.split('(')[0]} (Invalid UTC offset)`;
        return;
    }
    // Get current UTC time
    const now = new Date();
    const utcTime = now.getTime() + (now.getTimezoneOffset() * 60000);

    // Calculate local time based on offset
    const localTime = new Date(utcTime + (3600000 * offset)); // 3600000 ms = 1 hour

    // Format date as YYYY-MM-DD
    const year = localTime.getFullYear();
    const month = String(localTime.getMonth() + 1).padStart(2, '0');
    const day = String(localTime.getDate()).padStart(2, '0');
    const formattedDate = `${year}-${month}-${day}`;

    // Format time as HH:MM:SS in military time
    const hours = String(localTime.getHours()).padStart(2, '0');
    const minutes = String(localTime.getMinutes()).padStart(2, '0');
    const seconds = String(localTime.getSeconds()).padStart(2, '0');
    const timePart = `${hours}:${minutes}:${seconds}`;

    // Update the text content with the calculated time
    element.textContent = `${element.textContent.split('(')[0]} (${utcOffset}h): ${formattedDate} ${timePart}`;
}

// Cleanup when page is unloaded
window.addEventListener('beforeunload', function() {
    clearInterval(updateInterval);
});

// Add event listeners to the delete buttons for the timezones in the landpage editor.
function addDeleteEventListener(button) {
    button.addEventListener('click', function() {
        fetch("/api/landpage/timezones/" + encodeURIComponent(button.getAttribute('id')), {
            method: 'DELETE'
        })
        .then(response => {
            if (response.ok) {
                document.getElementById("card-" + button.getAttribute('id')).remove();
                console.log('Button deleted successfully');
            } else {
                console.error('Failed to delete button');
            }
        })
        .catch(error => {
            console.error('Error:', error);
        });
    });
}

// Add event listeners to all of the 'delete' buttons on the timezone cards.
document.querySelectorAll('.close-btn').forEach(button => {
    addDeleteEventListener(button);
});

// Add the selected timezone to database and reflect that change on the edit landpage editor.
function addZone(selectElement) {
    let selectedValue = selectElement.value;

    fetch("/api/landpage/timezones/", {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json;charset=UTF-8'
        },
        body: JSON.stringify({ id: selectedValue })
    })
    .then(response => {
        if (response.ok) {
            console.log('POST request successful');

            // Add the new HTML code to the page
            let placeholder = document.getElementById('add-card');
            let newCard = document.createElement('div');
            newCard.className = 'card';
            newCard.id = 'card-' + selectedValue;

            // Get the selected option
            let selectedOption = selectElement.options[selectElement.selectedIndex];
            let offset = selectedOption.dataset.offset;

            newCard.innerHTML = `
                <div class="card-content">
                    <button type="button" class="close-btn" id="{timezone.identifier}">
                        <i class="bi bi-x-lg"></i>
                    </button>
                    <h3 class="card-title">${selectedValue}</h3>
                    <p class="card-description" data-offset="${offset}"></p>
                </div>
            `;

            // Place new timezone card before the "Add Timezone" card.
            placeholder.parentNode.insertBefore(newCard, placeholder.previousSibling);
            addDeleteEventListener(document.getElementById(selectedValue));

            // Force refresh css.
            forceReflow();
        } else {
            console.error('Failed to send POST request');
        }
    })
    .catch(error => {
        console.error('Error:', error);
    });
}

// Force the reload of css after the html is modified.
function forceReflow() {
    var body = document.body;
    // Adding and immediately removing a dummy style triggers reflow
    body.style.display = 'none';
    body.offsetHeight; // Force reflow for Chrome, Firefox, IE11
    body.style.display = '';
}

// ====================================================
// Functions etc for message viewer/editor
// ====================================================
var currentMessage;
var currentDialogType;

function stripQuotes(str) {
    if (str.startsWith('"') || str.startsWith("'")) {
        str = str.slice(1);
    }
    if (str.endsWith('"') || str.endsWith("'")) {
        str = str.slice(0, -1);
    }
    return str;
}

function showDetails(messageId) {
    const jsonmessage = document.getElementById('message-row-' + messageId).dataset.messageData;

    console.log("View JSON Message: " + jsonmessage);

    const message = JSON.parse(stripQuotes(jsonmessage));

    document.getElementById('subject').innerText = message.subject;
    document.getElementById('message').innerText = message.content;
    document.getElementById('priority').innerText = message.priority;
    document.getElementById('startDate').innerText = message.startDate;
    document.getElementById('endDate').innerText = message.endDate;
    document.getElementById('lastUpdatedBy').innerText = message.lastUpdatedBy;
    document.getElementById('lastUpdated').innerText = message.lastUpdated;
    document.getElementById('createdBy').innerText = message.createdBy;
    document.getElementById('createdAt').innerText = message.createdAt;
}

function showNotification(message, type) {
    // 'type' should be 'danger', 'success', etc.
    var alertHtml = '<div class="alert alert-' + type + ' alert-dismissible fade show" role="alert">' +
                    message +
                    '<button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>' +
                    '</div>';
    var $alert = $(alertHtml);
    $('#notification-container').append($alert);

    // Auto hide the notification after 5 seconds
    setTimeout(function() {
        // Use Bootstrap's alert 'close' method
        $alert.alert('close');
    }, 5000);
}

function showEditDetails(dialogType, messageId) {
    let jsonmessage;

    currentDialogType = dialogType;
    if (dialogType == 'create') {
        console.log("dialogType: " + dialogType);
        jsonmessage = '{"id":"","subject":"","priority":"1","startDate":"","endDate":"","createdBy":"","createdAt":"","lastUpdatedBy":"","lastUpdated":"","content":""}';
        console.log("Create JSON Message: " + jsonmessage);
    } else {
        console.log("dialogType: " + dialogType);
        jsonmessage = document.getElementById('message-row-' + messageId).dataset.messageData;
        console.log("Update JSON Message: " + jsonmessage);
    }

    currentMessage = JSON.parse(jsonmessage);

    document.getElementById('subject').value = currentMessage.subject;
    document.getElementById('message').value = currentMessage.content;
    document.getElementById('priority').value = currentMessage.priority;
    document.getElementById('startDate').value = currentMessage.startDate;
    document.getElementById('endDate').value = currentMessage.endDate;
    document.getElementById('lastUpdatedBy').value = currentMessage.lastUpdatedBy;
    document.getElementById('lastUpdated').value = currentMessage.lastUpdated;
    document.getElementById('createdBy').value = currentMessage.createdBy;
    document.getElementById('createdAt').value = currentMessage.createdAt;
}

function saveMessage() {
    currentMessage.subject = document.getElementById('subject').value;
    currentMessage.content = document.getElementById('message').value;
    currentMessage.priority = document.getElementById('priority').value;
    currentMessage.startDate = document.getElementById('startDate').value;
    currentMessage.endDate = document.getElementById('endDate').value;
    currentMessage.lastUpdatedBy = document.getElementById('lastUpdatedBy').value;
    currentMessage.lastUpdated = document.getElementById('lastUpdated').value;
    currentMessage.createdBy = document.getElementById('createdBy').value;
    currentMessage.createdAt = document.getElementById('createdAt').value;

    if (currentDialogType == 'create') {
        insertMessage(currentMessage);
    } else {
        updateMessage(currentMessage);
    }
}

function refreshTable() {
    fetch('/api/landpage/messages-table')
    .then(response => {
        if (!response.ok) {
            throw new Error('Error refreshing table: ' + response.statusText);
        }
        return response.text();
    })
    .then(htmlFragment => {
        document.querySelector('#message-table table tbody').innerHTML = htmlFragment;
    })
    .catch(error => {
        console.error('Error refreshing table:', error);
        showNotification('Error refreshing table: ' + error, 'danger');
    });
}

function insertMessage(message) {
    fetch('/api/landpage/message', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(message)
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Error saving message: ' + response.statusText);
        }
        return response.json();
    })
    .then(responseData => {
        refreshTable(); // Refresh the table with updated data
        let modal = bootstrap.Modal.getInstance(document.getElementById('editModal'));
        modal.hide();
        console.log('Message saved successfully:', responseData);
        showNotification('Message saved successfully', 'success');
    })
    .catch(error => {
        console.error('Error saving message:', error);
        showNotification('Error saving message: ' + error, 'danger');
    });
}

function updateMessage(message) {
    fetch('/api/landpage/message/' + message.id, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(message)
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Error updating message: ' + response.statusText);
        }
        return response.json();
    })
    .then(responseData => {
        refreshTable(); // Refresh the table with updated data
        let modal = bootstrap.Modal.getInstance(document.getElementById('editModal'));
        modal.hide();
        console.log('Message updated successfully:', responseData);
        showNotification('Message updated successfully', 'success');
    })
    .catch(error => {
        console.error('Error updating message:', error);
        showNotification('Error updating message: ' + error, 'danger');
    });
}

// Instead of a native confirm, show a custom delete confirmation modal.
function deleteMessage() {
    if (currentDialogType === 'create') {
        return;
    }
    $('#confirmDeleteMessageSubject').text(currentMessage.subject);

    // Show the confirmation modal
    new bootstrap.Modal(document.getElementById('confirmDeleteModal')).show();
}

// Called when user confirms deletion in the custom confirmation modal.
function confirmDelete() {
    fetch('/api/landpage/message/' + currentMessage.id, {
        method: 'DELETE'
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Error deleting message: ' + response.statusText);
        }
        return response.text();
    })
    .then(responseData => {
        console.log('Message deleted successfully:', responseData);
        refreshTable();
        let editModal = bootstrap.Modal.getInstance(document.getElementById('editModal'));
        if (editModal) editModal.hide();
        let confirmModal = bootstrap.Modal.getInstance(document.getElementById('confirmDeleteModal'));
        if (confirmModal) confirmModal.hide();
        showNotification('Message deleted successfully', 'success');
    })
    .catch(error => {
        console.error('Error deleting message:', error);
        showNotification('Error deleting message: ' + error, 'danger');
    });
}

// Initialize jQuery Validate on the form
$(document).ready(function() {
    $("#message-form").validate({
        rules: {
            subject: {
                required: true,
                maxlength: 255
            },
            message: {
                required: true,
                maxlength: 1024
            },
            priority: "required",
            startDate: "required",
            endDate: "required"
        },
        messages: {
            subject: {
                required: "Please enter a subject",
                maxlength: "The subject must be no longer than 255 characters"
            },
            message: {
                required: "Please enter a message",
                maxlength: "The message must be no longer than 1024 characters"
            },
            priority: "Please select a priority",
            startDate: "Please select a start date",
            endDate: "Please select an end date"
        },
        errorClass: "is-invalid",
        validClass: "is-valid",
        errorElement: "div",
        errorPlacement: function(error, element) {
            error.addClass("invalid-feedback");
            error.insertAfter(element);
        },
        highlight: function(element, errorClass, validClass) {
            $(element).addClass(errorClass).removeClass(validClass);
        },
        unhighlight: function(element, errorClass, validClass) {
            $(element).removeClass(errorClass).addClass(validClass);
        }
    });
});
