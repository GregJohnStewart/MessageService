let updateInterval;

function openHelpfulLink(link) {
    let newWindow = window.open(link, "_blank", "width=800,height=800");
}

function startUpdatingTimes() {
    updateAllTimes();
    // Update every 1 second (1000 milliseconds)
    updateInterval = setInterval(updateAllTimes, 1000);
}

function updateAllTimes() {
    // Now targeting the container with the updated display structure.
    document.querySelectorAll('.timezone-display[data-offset]').forEach(container => {
        const offset = container.getAttribute('data-offset');
        updateSingleTime(container, offset);
    });
}

function updateSingleTime(container, utcOffset) {
    let offset = parseFloat(utcOffset);

    // Check if the offset is valid
    if (isNaN(offset)) {
        container.innerHTML = `<div class="timezone-time">Invalid UTC offset</div>`;
        return;
    }
    // Get current UTC time and calculate local time based on the offset
    const now = new Date();
    const utcTime = now.getTime() + (now.getTimezoneOffset() * 60000);
    const localTime = new Date(utcTime + (3600000 * offset)); // 3600000 ms = 1 hour

    // Format time as HH:MM
    const hours = String(localTime.getHours()).padStart(2, '0');
    const minutes = String(localTime.getMinutes()).padStart(2, '0');
    const timeDisplay = `${hours}:${minutes}`;

    // Format date as YYYY-MM-DD
    const day = String(localTime.getDate()).padStart(2, '0');
    const month = String(localTime.getMonth() + 1).padStart(2, '0');
    const year = localTime.getFullYear();
    const dateDisplay = `${year}-${month}-${day}`;

    // Update the child elements of the container
    const timeElem = container.querySelector('.timezone-time');
    if (timeElem) {
        timeElem.textContent = timeDisplay;
    }
    const dateElem = container.querySelector('.timezone-date');
    if (dateElem) {
        dateElem.textContent = dateDisplay;
    }
}

function sortTimezoneCards() {
    // Get the container holding all the timezone cards
    const container = document.querySelector('.card-container');
    if (!container) return;
  
    // Get an array of all timezone cards (exclude the "add-card")
    const cards = Array.from(container.querySelectorAll('.card:not(#add-card)'));
    
    // Sort the cards by the numerical value of the offset
    cards.sort((a, b) => {
      const offsetA = parseFloat(a.querySelector('.timezone-display').getAttribute('data-offset'));
      const offsetB = parseFloat(b.querySelector('.timezone-display').getAttribute('data-offset'));
      return offsetB - offsetA;
    });
    
    // Remove the sorted cards from the container
    cards.forEach(card => card.remove());
    
    // Reinsert the sorted cards into the container (at the beginning)
    cards.forEach(card => container.insertBefore(card, container.firstChild));
    
    // Finally, ensure the "add-card" is at the end
    const addCard = document.getElementById('add-card');
    if (addCard) {
      addCard.remove();
      container.appendChild(addCard);
    }
}
  
// Call this function after the timezone cards are rendered.
// For example, in your onload handler:
window.addEventListener('load', () => {
    startUpdatingTimes();
    updateTimezoneNames();
    sortTimezoneCards();
});
  
function formatTimezoneName(name) {

    // Find the first forward slash
    const slashIndex = name.lastIndexOf('/');
    // If found, return the substring from the slash (inclusive) in upper case.
    // Otherwise, return the full name in upper case.
    const uppercaseName = slashIndex !== -1 ? name.slice(slashIndex + 1).toUpperCase() : name.toUpperCase();

    // Replace underscores with spaces
    const timezoneName = uppercaseName.replace(/_/g, ' ');

    return timezoneName;
}

function updateTimezoneNames() {
    document.querySelectorAll('.timezone-name').forEach(elem => {
      elem.textContent = formatTimezoneName(elem.textContent);
    });
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
                console.log('Timezone deleted successfully');
            } else {
                showNotification('Timezone not found', 'warning');
                console.error('Timezone not found');
            }
        })
        .catch(error => {
            showNotification('Error deleting timezone: ' + error, 'danger');
            console.error('Error deleting timezone:', error);
        });
    });
}

// Attach delete event listeners to all of the 'delete' buttons on the timezone cards.
document.querySelectorAll('.close-btn').forEach(button => {
    addDeleteEventListener(button);
});

// Add the selected timezone to the database and reflect that change on the edit landpage editor.
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
            // Add the new HTML code to the page with the updated display structure.
            let placeholder = document.getElementById('add-card');
            let newCard = document.createElement('div');
            newCard.className = 'card';
            newCard.id = 'card-' + selectedValue;

            // Get the selected option and its UTC offset
            let selectedOption = selectElement.options[selectElement.selectedIndex];
            let offset = selectedOption.dataset.offset;
            let formattedTimezoneName = formatTimezoneName(selectedValue);

            newCard.innerHTML = `
                <div class="card-content">
                    <button type="button" class="close-btn" id="${selectedValue}">
                        <i class="bi bi-x-lg"></i>
                    </button>
                    <div class="timezone-display" data-offset="${offset}">
                        <div class="timezone-time"></div>
                        <div class="timezone-name">${formattedTimezoneName}</div>
                        <div class="timezone-date"></div>
                    </div>
                </div>
            `;

            // Place the new timezone card before the "Add Timezone" card.
            placeholder.parentNode.insertBefore(newCard, placeholder.previousSibling);
            addDeleteEventListener(document.getElementById(selectedValue));

            // Force a CSS reflow if needed.
            forceReflow();

            sortTimezoneCards();
        } else {
            showNotification('Timezone already exists', 'info');
            console.error('Timezone already exists');
        }
    })
    .catch(error => {
        showNotification('Error adding timezone: ' + error, 'danger');
        console.error('Error adding timezone:', error);
    });
}

// Force the reload of CSS after the HTML is modified.
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
    // 'type' should be one of the following: 
    // 'primary', 'secondary', 'success', 'danger', 'warning', 'info', 'light', 'dark'
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
        jsonmessage = '{"id":"","subject":"","priority":"1","startDate":"","endDate":"","createdBy":"","createdAt":"","lastUpdatedBy":"","lastUpdated":"","content":""}';
    } else {
        jsonmessage = document.getElementById('message-row-' + messageId).dataset.messageData;
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
