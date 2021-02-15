
document.addEventListener('DOMContentLoaded', function () {
    ajax_get('/entries', function(data) {
        populateMainTable(data.library_entries);
    });
});

/*
Use Event bubbling to add event listeners to current and future button elements from the document object.
 */
//TODO: Make single button click function and pass in edit/delete/update param
document.addEventListener('click',function(e) {

    if (getBtnIdDescription(e.target.id) === ('delete')) {
        let id = getBtnIdNum(e.target.id);
        let xhr = new XMLHttpRequest();
        let url = '/delete/' + id;
        xhr.open('POST', url, true);

        //Send the proper header information along with the request
        xhr.setRequestHeader('Content-type', 'application/json');
        xhr.onreadystatechange = function () {
            if (xhr.readyState === 4 && xhr.status === 200) {
                console.log("deleted id: " + xhr.responseText);
                deleteRowOfBtnClick(e.target);
                clearNewEntryForm('addEditForm');
            }
        }
        xhr.send();
    }
});


document.addEventListener('click',function(e) {
    if (getBtnIdDescription(e.target.id) === ('edit')) {
        let id = getBtnIdNum(e.target.id);
        let url = '/entry/' + id;

        ajax_get(url, function(data) {
            console.log(data);
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
        })
    }
});


function populateMainTable(data) {
    let table = document.getElementById('table_all');
    for (let i = 0; i < data.length; i++) {
        addEditRowMain(table.insertRow(), data[i]);
        table.rows[table.rows.length - 1 ].classList.add('row', 'row--body');
    }
}


function addEditRowMain(row, data) {
    let rowContent = "<td class='cell'>" + data.id + "</td>";
        rowContent += "<td class='cell'>" + data.title + "</td>";
        rowContent += "<td class='cell'>" + data.composerFirstName + "</td>";
        rowContent += "<td class='cell'>" + data.composerLastName + "</td>";
        rowContent += "<td class='cell'>" + data.arranger + "</td>";
        rowContent += "<td class='cell'>" + data.voiceParts + "</td>";
        rowContent += "<td class='cell'>" + data.accompanied + "</td>";
        rowContent += "<td class='cell'>" + data.season + "</td>";
        rowContent += "<td class='cell'>" + data.seasonAdditional + "</td>";
        rowContent += "<td class='cell'>" + data.location + "</td>";
        rowContent += "<td class='cell'>" + data.collection + "</td>";
        rowContent += "<td class='cell flexwrapper'>" +
            "<a href='#addEdit' class='btn btn--primary btn--table' id='edit_" + data.id + "'>Edit</a>" +
            "<button class='btn btn--secondary btn--table' id='delete_" + data.id + "'>Delete</button>"
            + "</td>"
    row.innerHTML = '';
    row.innerHTML = rowContent;
}

function clearNewEntryForm() {
    document.getElementById('addEditForm').reset();
    document.getElementById('id').value = -1;
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
    if (document.getElementById('id').value == -1) {
        console.log(document.getElementById('id').value);
        let url = '/newentry';
        xhr.open('POST', url, true);

        //Send the proper header information along with the request
        xhr.setRequestHeader('Content-type', 'application/json');
        xhr.onreadystatechange = function () {
            if (xhr.readyState === 4 && xhr.status === 200) {
                let data = JSON.parse(xhr.responseText);
                console.log(data);
                let table = document.getElementById('table_all');
                addEditRowMain(table.insertRow(), data);
                table.rows[table.rows.length - 1 ].classList.add('row', 'row--body');
                clearNewEntryForm();
            }
        }
    } else {
        // CODE FOR UPDATING ENTRY
        console.log(object.id);
        let url = '/edit/' + object.id;
        xhr.open('POST', url, true);

        //Send the proper header information along with the request
        xhr.setRequestHeader('Content-type', 'application/json');
        xhr.onreadystatechange = function() {
            if(xhr.readyState === 4 && xhr.status === 200) {
                let data = JSON.parse(xhr.responseText);
                console.log(data);
                let btn = document.getElementById('edit_' + document.getElementById('id').value);
                updateRowOfBtnClick(btn, data);
                clearNewEntryForm();
            }
        }
    }
    xhr.send(json);
});

document.getElementById('btn_cancelNewEntry').addEventListener('click', function() {
    clearNewEntryForm();
    window.location.assign('/');
})

function getBtnIdNum(string) {
    return string.split('_')[1];
}

function getBtnIdDescription(string) {
    return string.split('_')[0];
}

function deleteRowOfBtnClick(btn) {
    let row = btn.parentNode.parentNode;
    row.parentNode.removeChild(row);
}

function updateRowOfBtnClick(btn, data) {
    let row = btn.parentNode.parentNode;
    addEditRowMain(row, data);
}


