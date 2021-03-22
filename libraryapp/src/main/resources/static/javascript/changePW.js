const LOGIN_SUCCESS = 'LOGIN_SUCCESS'; // Must match PSFS LOGIN_SUCCESS in GlorianaApplication.java
const LOGIN_FAIL = 'LOGIN_FAIL';// Must match PSFS LOGIN_FAIL in GlorianaApplication.java

document.getElementById('btn_pw').addEventListener("click", function (e) {
    e.preventDefault();
    let user = document.getElementById('select-user').value;
    let passwordOld = document.getElementById('oldPassword').value;
    let passwordNew1 = document.getElementById('newPassword1').value;
    let passwordNew2 = document.getElementById('newPassword2').value;

    if (passwordNew1 !== passwordNew2) {
        document.getElementById('changePW-failed').innerText = "Please ensure new passwords match";
        document.getElementById('changePW-failed').classList.remove('displayNone');
        return;
    }

    if (passwordOld === passwordNew1 || passwordOld === papasswordNew2) {
        document.getElementById('changePW-failed').innerText = "Please ensure new password is different to the current password";
        document.getElementById('changePW-failed').classList.remove('displayNone');
        return;
    }


    let object = {"user": user, "passwordOld" : passwordOld, "passwordNew1": passwordNew1, "passwordNew2": passwordNew2};
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

                // if logged in
                if (xhr.responseText === LOGIN_SUCCESS) {
                    console.log('logged in!');
                    sessionStorage.setItem('display-message', 'loggedin');
                    document.getElementById('login-failed').classList.add('displayNone');
                    window.location.href = '/';
                } else {
                    console.log('logged failed');
                    document.getElementById('login-failed').innerText = xhr.responseText;
                    document.getElementById('login-failed').classList.remove('displayNone');
                }
            } catch (err) {
                console.log(err.message + " in " + xhr.responseText);
            }
        }
    }
    xhr.send(json);
})

document.getElementById('login__close').addEventListener('click', function() {
    document.getElementById('loginForm').reset();
})