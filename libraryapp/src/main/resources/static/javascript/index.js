let deleteValue = -1;
let clickedBtn = undefined;

document.addEventListener('DOMContentLoaded', function () {
    ajax_get('/entries', function(data) {
        populateMainTable(data.library_entries);
    });

    document.getElementById('accompanied').addEventListener('click', function(e) {
        toggleLabelPlaceholderStyle(document.getElementById('accompanied'));
        document.getElementById('accompanied').style.color = 'inherit';
    });

    document.getElementById('season').addEventListener('click', function(e) {
        toggleLabelPlaceholderStyle(document.getElementById('season'));
        document.getElementById('season').style.color = 'inherit';
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
    document.getElementById('accompanied-label').style.display = "none";
    document.getElementById('accompanied').style.color = '#838083'; // COLOR_LIGHT_GREY
    document.getElementById('season-label').style.display = "none";
    document.getElementById('season').style.color = '#838083'; // COLOR_LIGHT_GREY

})

document.getElementById('accompanied').addEventListener('click', function() {
    toggleAddEditText();
    if (window.matchMedia("(max-width: 700px)").matches) {
        document.getElementById('accompanied-label').style.display = "none";
    } else {
        document.getElementById('accompanied-label').style.display = "block";
    }
})


document.getElementById('season').addEventListener('click', function() {
    toggleAddEditText();
    if (window.matchMedia("(max-width: 700px)").matches) {
        document.getElementById('season-label').style.display = "none";
    } else {
        document.getElementById('season-label').style.display = "block";
    }
})

/*
Use Event bubbling to add event listeners to current and future button elements from the document object.
 */
document.addEventListener('click',function(e) {
    if (getBtnIdDescription(e.target.id) === ('delete')) {
        //let status = getLoginStatus();
        console.log(getLoginStatus() === 1);
        if (getLoginStatus() !== 1) {
            alert("You don't have permission to do that!");
        } else {
            document.getElementById('delete-alert').classList.add('visible');
            // Set params for delete ajax
            deleteValue = getBtnIdNum(e.target.id);
            clickedBtn = e.target;
        }
    }
});


document.getElementById('btn_cancelDelete').addEventListener('click', function() {
    document.getElementById('delete-alert').classList.remove('visible');
    // Reset delete params
    deleteValue = -1;
    clickedBtn = undefined;
})


document.getElementById('btn_confirmDelete').addEventListener('click', function() {
    if (getLoginStatus() !== 1) {
        alert("You don't have permission to do that!")
    } else {
        document.getElementById('delete-alert').classList.remove('visible');
        let xhr = new XMLHttpRequest();
        let url = '/delete/' + deleteValue;
        xhr.open('POST', url, true);

        //Send the proper header information along with the request
        xhr.setRequestHeader('Content-type', 'application/json');
        xhr.onreadystatechange = function () {
            if (xhr.readyState === 4 && xhr.status === 200) {
                deleteRowOfBtnClick(clickedBtn);
                sessionStorage.setItem('display-message', 'deleted')
                clearNewEntryForm(true);

                // Reset delete params
                deleteValue = -1;
                clickedBtn = undefined;
            }
        }
        xhr.send();
    }
})


document.addEventListener('click',function(e) {
    if (getBtnIdDescription(e.target.id) === ('edit')) {
        let id = getBtnIdNum(e.target.id);
        let url = '/entry/' + id;

        ajax_get(url, function(data) {
            document.getElementById('id').value = data.id;
            document.getElementById('title').value = data.title;
            document.getElementById('composerLastName').value = data.composerLastName;
            document.getElementById('composerFirstName').value = data.composerFirstName;
            document.getElementById('arranger').value = data.arranger;
            document.getElementById('voiceParts').value = data.voiceParts;
            document.getElementById('season').value = data.season == null ? '-1' : data.season;
            document.getElementById('seasonAdditional').value = data.seasonAdditional;
            document.getElementById('location').value = data.location;
            document.getElementById('collection').value = data.collection;
            document.getElementById('accompanied').value = data.accompanied == null ? '-1' : data.accompanied;

            if (document.getElementById('accompanied').value === '') {
                document.getElementById('accompanied-label').style.display = "none";
                document.getElementById('accompanied').selectedIndex = 0;
                document.getElementById('accompanied').style.color = '#838083'; // COLOR_LIGHT_GREY
            } else {
                if (window.matchMedia("(max-width: 700px)").matches) {
                    document.getElementById('accompanied-label').style.display = "none";
                } else {
                    document.getElementById('accompanied-label').style.display = "block";
                }
            }

            if (document.getElementById('season').value === '') {
                document.getElementById('season-label').style.display = "none";
                document.getElementById('season').selectedIndex = 0;
                document.getElementById('season').style.color = '#838083'; // COLOR_LIGHT_GREY
            } else {
                if (window.matchMedia("(max-width: 700px)").matches) {
                    document.getElementById('season-label').style.display = "none";
                } else {
                    document.getElementById('season-label').style.display = "block";
                }
            }

            toggleLabelPlaceholderStyle(document.getElementById('accompanied'));
            toggleLabelPlaceholderStyle(document.getElementById('season'));
            toggleAddEditText();
        });
    }
});


function populateMainTable(data) {
    let tableBody = document.getElementById('table__body');
    for (let i = 0; i < data.length; i++) {
        addRowMainTable(tableBody, data[i]);
    }
}


function addRowMainTable(tableBody, data) {
    // CREATE NEW ROW
    let row = document.createElement('div');
    row.classList.add('row', 'row--body', 'row--visible');
    let containerDiv;
    tableBody.appendChild(row);

    // ADD SUB-ROW DIVS (DETERMINED BY DESIGN), FOR RESPONSIVE BEHAVIOUR AT SMALLER SCREEN SIZES
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

        // ADD AND APPEND CELL TO ROW
        let cell = document.createElement('div');
        cell.classList.add('cell', 'flex__full');
        cell.classList.add(tableParameters[i]);
        containerDiv.appendChild(cell);

        // APPEND DATA TO CELL
        let input = data[Object.keys(data)[i]];
        if (input === undefined) {
            cell.innerText = ' ';
        } else {
            cell.innerText = input;
        }
    }

    // FINALLY CREATE CELL WITH ACTION BUTTONS
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


function clearNewEntryForm(boolRefresh) {
    document.getElementById('addEditForm').reset();
    document.getElementById('id').value = '-1';
    if (boolRefresh) {
        window.location = '/';
    } else {
        window.location = '/#';
    }
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


document.getElementById('addEditForm').addEventListener("submit", function (e) {
    e.preventDefault();

    let formData = new FormData(this);
    let object = {};
    formData.forEach(function(value, key){
        object[key] = value;
    });
    let json = JSON.stringify(object);
    let xhr = new XMLHttpRequest();

    if (document.getElementById('id').value === '-1') {
        addEdit__add(xhr);
    } else {
        addEdit__edit(xhr);
    }
    xhr.send(json);
});


function addEdit__add(xhr) {
    if (document.getElementById('id').value === '-1') {
        let url = '/newentry';
        xhr.open('POST', url, true);

        //Send the proper header information along with the request
        xhr.setRequestHeader('Content-type', 'application/json');
        xhr.onreadystatechange = function () {
            if (xhr.readyState === 4 && xhr.status === 200) {
                let data = JSON.parse(xhr.responseText);
                let tableBody = document.getElementById('table__body');
                addRowMainTable(tableBody, data);
                sessionStorage.setItem('display-message', 'added');
                console.log(sessionStorage.getItem('display-message'));
                clearNewEntryForm(true);
            }
        }
    }
}


function addEdit__edit(xhr) {
    let url = '/edit';
    xhr.open('POST', url, true);

    //Send the proper header information along with the request
    xhr.setRequestHeader('Content-type', 'application/json');
    xhr.onreadystatechange = function() {
        if(xhr.readyState === 4 && xhr.status === 200) {
            let data = JSON.parse(xhr.responseText);
            let btn = document.getElementById('edit_' + document.getElementById('id').value);
            updateRowOfBtnClick(btn, data);
            sessionStorage.setItem('display-message', 'updated')
            clearNewEntryForm(true);
        }
    }
}


document.getElementById('btn_cancelNewEntry').addEventListener('click', function() {
    clearNewEntryForm(false);
});


document.getElementById('search__button').addEventListener("click", function (e) {
    e.preventDefault();
    document.getElementById('message-searchNotFound').style.display = 'none';
    let searchString = document.getElementById('search__input').value;
    if (searchString === '') {
        ajax_get('/entries', function (data) {
            populateMainTable(data.library_entries);
            window.location = '/';
        });
    } else {
        let xhr = new XMLHttpRequest();
        let url = '/searchentries';
        xhr.open('POST', url, true);

        //Send the proper header information along with the request
        xhr.setRequestHeader('Content-type', 'application/json');
        xhr.onreadystatechange = function() {
            if (xhr.readyState === 4 && xhr.status === 200) {
                let data = JSON.parse(xhr.responseText);
                if (data.library_entries.length === 0) {
                    document.getElementById('message-searchNotFound').style.display = 'inline-block';
                }
                clearClassFromDOM('row--visible');
                populateMainTable(data.library_entries);
                document.getElementById('search__input--hamburger').value = searchString;
            }
        }
        xhr.send(searchString);
    }
});


document.getElementById('search__button--hamburger').addEventListener("click", function (e) {
    e.preventDefault();
    document.getElementById('message-searchNotFound').style.display = 'none';
    let searchString = document.getElementById('search__input--hamburger').value;

    if (searchString === '') {
        ajax_get('/entries', function(data) {
            populateMainTable(data.library_entries);
            window.location = '/';
        });
    } else {
        let xhr = new XMLHttpRequest();
        let url = '/searchentries';
        xhr.open('POST', url, true);

        //Send the proper header information along with the request
        xhr.setRequestHeader('Content-type', 'application/json');
        xhr.onreadystatechange = function() {
            if (xhr.readyState === 4 && xhr.status === 200) {
                let data = JSON.parse(xhr.responseText);
                if (data.library_entries.length === 0) {
                    document.getElementById('message-searchNotFound').style.display = 'inline-block';
                } else {
                    clearClassFromDOM('row--visible');
                    populateMainTable(data.library_entries);
                    document.getElementById('search__input').value = searchString;
                    window.location = '/#';
                }
            }
        }
        xhr.send(searchString);
    }
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
    addRowMainTable(row, data);
}


document.getElementById('form__close').addEventListener('click', function() {
    clearNewEntryForm(false);
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
    clearSearchForms();
    window.location = '/';
})


document.getElementById('search__clear--hamburger').addEventListener('click', function() {
    clearSearchForms();
})


function clearSearchForms() {
    document.getElementById('searchForm').reset();
    document.getElementById('searchForm--hamburger').reset();
}


document.getElementById('searchMusic').addEventListener('click', function() {
    document.getElementById('search--hamburger').style.display = 'flex';
})


const tableParameters = ["pseudo--ID", "pseudo--title", "pseudo--lastName", "pseudo--firstName", "pseudo--arranger",
    "pseudo--voiceParts", "pseudo--accompanied", "pseudo--season", "pseudo--seasonAdditional", "pseudo--location",
    "pseudo--collection", "pseudo--action"];



// AUTHENTICATION
document.getElementById('btn_login').addEventListener("click", function (e) {
    e.preventDefault();
    let username =  document.getElementById('username').value;
    let password =  document.getElementById('password').value;

    let object = {"username": username, "password" : password};
    let json = JSON.stringify(object);
    console.log(json);

    let xhr = new XMLHttpRequest();
    let url = '/login';
    xhr.open('POST', url, true);

    //Send the proper header information along with the request
    xhr.setRequestHeader('Content-type', 'application/json');
    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4 && xhr.status === 200) {
            alert("Successfully logged in as ${xhr.responseText}");
        }
    }
    xhr.send(json);
})

function getLoginStatus() {
    let authStatus = -1;
    ajax_get('/loginstatus', function(data) {
        console.log(data + "hi");
        authStatus = data;
    });
    return authStatus;
}