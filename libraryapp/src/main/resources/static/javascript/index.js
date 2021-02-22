let deleteValue = -1;
let clickedBtn = undefined;

document.addEventListener('DOMContentLoaded', function () {
    ajax_get('/entries', function(data) {
        populateMainTable(data.library_entries);
    });

    document.getElementById('accompanied').addEventListener('click', function(e) {
        toggleLabelPlaceholderStyle(document.getElementById('accompanied'));
    });
});

window.addEventListener('load', function(event) {
    if (sessionStorage.getItem('display-message') !== 'undefined') {
        document.getElementById('message-' + sessionStorage.getItem('display-message')).style.display = 'inline-block';
    }
    sessionStorage.setItem('display-message', undefined);
});

document.getElementById('btn_createNewEntry').addEventListener('click', function() {
    toggleAddEditText();
})

/*
Use Event bubbling to add event listeners to current and future button elements from the document object.
 */
//TODO: Make single button click function and pass in edit/delete/update param
document.addEventListener('click',function(e) {

    if (getBtnIdDescription(e.target.id) === ('delete')) {
        document.getElementById('delete-alert').classList.add('visible');

        // Set params for delete ajax
        deleteValue = getBtnIdNum(e.target.id);
        clickedBtn = e.target;
    }
});


document.getElementById('btn_cancelDelete').addEventListener('click', function() {
    document.getElementById('delete-alert').classList.remove('visible');
    // Reset delete params
    deleteValue = -1;
    clickedBtn = undefined;
})

document.getElementById('btn_confirmDelete').addEventListener('click', function() {
    document.getElementById('delete-alert').classList.remove('visible');
    let xhr = new XMLHttpRequest();
    let url = '/delete/' + deleteValue;
    xhr.open('POST', url, true);

    //Send the proper header information along with the request
    xhr.setRequestHeader('Content-type', 'application/json');
    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4 && xhr.status === 200) {
            deleteRowOfBtnClick(clickedBtn);
            // document.getElementById('message-deleted').style.display = 'inline-block';
            sessionStorage.setItem('display-message', 'deleted')
            clearNewEntryForm();

            // Reset delete params
            deleteValue = -1;
            clickedBtn = undefined;
        }
    }
    xhr.send();
})




document.addEventListener('click',function(e) {
    if (getBtnIdDescription(e.target.id) === ('edit')) {
        let id = getBtnIdNum(e.target.id);
        let url = '/entry/' + id;

        ajax_get(url, function(data) {
            document.getElementById('id').value = data.id;
            document.getElementById('title').value = data.title;
            document.getElementById('composerFirstName').value = data.composerFirstName;
            document.getElementById('composerLastName').value = data.composerLastName;
            document.getElementById('arranger').value = data.arranger;
            document.getElementById('voiceParts').value = data.voiceParts;
            document.getElementById('season').value = data.season;
            document.getElementById('seasonAdditional').value = data.seasonAdditional;
            document.getElementById('location').value = data.location;
            document.getElementById('collection').value = data.collection;
            document.getElementById('accompanied').value = data.accompanied == null ? '-1' : data.accompanied;

            toggleLabelPlaceholderStyle(document.getElementById('accompanied'));
            toggleAddEditText();
        });
    }
});


function populateMainTable(data) {
    let tableBody = document.getElementById('table__body');
    for (let i = 0; i < data.length; i++) {
        addEditRowMain(tableBody, data[i]);
    }
}

function addEditRowMain(tableBody, data) {
    let row = document.createElement('div');
    row.classList.add('row', 'row--body', 'row--visible');
    let containerDiv;
    tableBody.appendChild(row);

    for (let i = 0; i < Object.keys(data).length; i++) {

        if (i === 0) {
            continue;
        }

        if (i === 1) {
            containerDiv = document.createElement('div')
            containerDiv.classList.add('table--name', 'flex__fiveElevenths', 'flex__oneHalfAtMediumBreak');
            row.appendChild(containerDiv);
        }

        if (i === 6) {
            containerDiv = document.createElement('div')
            containerDiv.classList.add('table--accompanied', 'flex__fiveElevenths', 'flex__oneHalfAtMediumBreak');
            row.appendChild(containerDiv);
        }


        let cell = document.createElement('div');
        cell.classList.add('cell', 'flex__full');
        cell.classList.add(tableParameters[i]);

        containerDiv.appendChild(cell);

        let input = data[Object.keys(data)[i]];
        if (input === undefined) {
            cell.innerText = ' ';
        } else {
            cell.innerText = input;
        }
    }


    containerDiv = document.createElement('div')
    containerDiv.classList.add('table--action', 'flex__oneEleventh');
    row.appendChild(containerDiv);

    let buttonDiv = document.createElement('div');
    buttonDiv.classList.add('cell', 'cell--action', 'flex__wrap');
    containerDiv.appendChild(buttonDiv);

    let editBtn = document.createElement('a');
    editBtn.classList.add('btn', 'btn--primary', 'btn--table');
    editBtn.id = "edit_" + data.id;
    editBtn.innerText = "Edit";
    editBtn.setAttribute("href", "#addEdit");
    buttonDiv.appendChild(editBtn);

    let deleteBtn = document.createElement('a');
    deleteBtn.classList.add('btn', 'btn--secondary', 'btn--table');
    deleteBtn.id = "delete_" + data.id;
    deleteBtn.innerText = "Delete";
    buttonDiv.appendChild(deleteBtn);
}


function clearNewEntryForm() {
    document.getElementById('addEditForm').reset();
    document.getElementById('id').value = '-1';
    window.location = '/';
}

/*
general ajax call to get json from url then pass it into a function.
 */
function ajax_get(url, callback) {
    let xhr = new XMLHttpRequest();
    let data;
    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4 && xhr.status === 200) {
            try {
                data = JSON.parse(xhr.responseText);
            } catch (err) {
                console.log(err.message + " in " + xhr.responseText);
                return;
            }
            callback(data);
        }
    };

    xhr.open("GET", url, true);
    xhr.send();
}

//TODO: Tidy this function up.
document.getElementById('addEditForm').addEventListener("submit", function (e) {
    e.preventDefault();

    let formData = new FormData(this);
    let object = {};
    formData.forEach(function(value, key){
        object[key] = value;
    });
    let json = JSON.stringify(object);
    let xhr = new XMLHttpRequest();

    // CODE FOR ADDING NEW ENTRY
    if (document.getElementById('id').value === '-1') {
        let url = '/newentry';
        xhr.open('POST', url, true);

        //Send the proper header information along with the request
        xhr.setRequestHeader('Content-type', 'application/json');
        xhr.onreadystatechange = function () {
            if (xhr.readyState === 4 && xhr.status === 200) {
                let data = JSON.parse(xhr.responseText);
                let tableBody = document.getElementById('table__body');
                addEditRowMain(tableBody, data);
                sessionStorage.setItem('display-message', 'added');
                console.log(sessionStorage.getItem('display-message'));
                // document.getElementById('message-added').style.display = 'inline-block';
                clearNewEntryForm();
            }
        }
    } else {
        // CODE FOR UPDATING ENTRY
        let url = '/edit/' + object.id;
        xhr.open('POST', url, true);

        //Send the proper header information along with the request
        xhr.setRequestHeader('Content-type', 'application/json');
        xhr.onreadystatechange = function() {
            if(xhr.readyState === 4 && xhr.status === 200) {
                let data = JSON.parse(xhr.responseText);
                let btn = document.getElementById('edit_' + document.getElementById('id').value);
                updateRowOfBtnClick(btn, data);
                sessionStorage.setItem('display-message', 'updated')
                // document.getElementById('message-updated').style.display = 'inline-block';
                clearNewEntryForm();
            }
        }
    }
    xhr.send(json);
});

document.getElementById('btn_cancelNewEntry').addEventListener('click', function() {
    clearNewEntryForm();
});

document.getElementById('search__button').addEventListener("click", function (e) {
    e.preventDefault();
    let searchString = document.getElementById('search__input').value;
    let xhr = new XMLHttpRequest();
    let url = '/searchentries';
    xhr.open('POST', url, true);

    //Send the proper header information along with the request
    xhr.setRequestHeader('Content-type', 'application/json');
    xhr.onreadystatechange = function() {
        if (xhr.readyState === 4 && xhr.status === 200) {
            let data = JSON.parse(xhr.responseText);
            clearClassFromDOM('row--visible');
            populateMainTable(data.library_entries);

        }
    }
    xhr.send(searchString);
});


function getBtnIdNum(string) {
    return string.split('_')[1];
}

function getBtnIdDescription(string) {
    return string.split('_')[0];
}

function deleteRowOfBtnClick(btn) {
    let row = btn.parentNode.parentNode.parentNode;
    row.parentNode.removeChild(row);
}

function updateRowOfBtnClick(btn, data) {
    let row = btn.parentNode.parentNode;
    addEditRowMain(row, data);
}

document.getElementById('form__close').addEventListener('click', function() {
    clearNewEntryForm();
});

document.getElementById('nav--burger').addEventListener('click', function() {
    window.location = '/#hamburger-menu';
});

function toggleLabelPlaceholderStyle(labelElement) {
    if (labelElement.value === '-1') {
        labelElement.classList.add('select-text-default');
    } else {
        labelElement.classList.remove('select-text-default');
    }
}

function toggleAddEditText() {
    if (document.getElementById('id').value === '-1') {
        document.getElementById('heading__form').innerHTML=('Add new entry');
        document.getElementById('btn_newEntry').innerHTML=('Add entry &rarr;');
    } else {
        document.getElementById('heading__form').innerHTML= 'Update entry';
        document.getElementById('btn_newEntry').innerHTML= 'Update entry &rarr;';
    }
}

function clearClassFromDOM(className) {
    let classList = document.getElementsByClassName(className);

    while(classList[0]) {
        classList[0].parentNode.removeChild(classList[0]);
    }
}

document.getElementById('search__clear').addEventListener('click', function() {
    document.getElementById('searchForm').reset();
    window.location = '/';
})

const tableParameters = ["pseudo--ID", "pseudo--title", "pseudo--firstName", "pseudo--lastName", "pseudo--arranger",
    "pseudo--voiceParts", "pseudo--accompanied", "pseudo--season", "pseudo--seasonAdditional", "pseudo--location",
    "pseudo--collection", "pseudo--action"];

