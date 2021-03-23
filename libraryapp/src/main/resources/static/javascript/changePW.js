const PW_CHANGE_SUCCESS_RESPONSE_VALUE = "PW_SUCCESS"; // Must match PSFS LOGIN_SUCCESS in GlorianaApplication.java

document.getElementById('btn_pw').addEventListener("click", function (e) {
    e.preventDefault();
    let user = document.getElementById('select-user').value;
    let passwordCurrent = document.getElementById('currentPassword').value;
    let passwordNew1 = document.getElementById('newPassword1').value;
    let passwordNew2 = document.getElementById('newPassword2').value;

    if (passwordNew1 !== passwordNew2) {
        document.getElementById('changePW-failed').innerText = "Please ensure new passwords match";
        document.getElementById('changePW-failed').classList.remove('displayNone');
        return;
    }

    if (passwordCurrent === passwordNew1 || passwordCurrent === passwordNew2) {
        document.getElementById('changePW-failed').innerText = "Please ensure the proposed new password is different to your current password";
        document.getElementById('changePW-failed').classList.remove('displayNone');
        return;
    }


    let object = {"user": user, "passwordCurrent" : passwordCurrent, "passwordNew1": passwordNew1, "passwordNew2": passwordNew2};
    let json = JSON.stringify(object);

    let xhr = new XMLHttpRequest();
    let url = '/changepw';
    xhr.open('POST', url, true);

    //Send the proper header information along with the request
    xhr.setRequestHeader('Content-type', 'application/json');
    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4 && xhr.status === 200) {
            try {
                console.log(xhr.responseText);
                document.getElementById('changePWForm').reset();

                // if success
                if (xhr.responseText === PW_CHANGE_SUCCESS_RESPONSE_VALUE) {
                    console.log('Changed PW');
                     sessionStorage.setItem('display-message', 'passwordchanged');
                     document.getElementById('changePW-failed').classList.add('displayNone');
                     window.location.href = '/';
                } else {
                    console.log('pw change failed');
                    document.getElementById('changePW-failed').innerText = xhr.responseText;
                    document.getElementById('changePW-failed').classList.remove('displayNone');
                }
            } catch (err) {
                console.log(err.message + " in " + xhr.responseText);
            }
        }
    }
    xhr.send(json);
})

document.getElementById('pw__close').addEventListener('click', function() {
    document.getElementById('changePWForm').reset();
    window.location.href = '/';
})