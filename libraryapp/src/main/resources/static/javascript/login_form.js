const LOGIN_SUCCESS = 'LOGIN_SUCCESS'; // Must match PSFS LOGIN_SUCCESS in GlorianaApplication.java
const LOGIN_FAIL = 'LOGIN_FAIL';// Must match PSFS LOGIN_FAIL in GlorianaApplication.java
const hello = 'hi';

document.getElementById('btn_login').addEventListener("click", function (e) {
    e.preventDefault();
    let username =  document.getElementById('username').value;
    let password =  document.getElementById('password').value;

    let object = {"username": username, "password" : password};
    let json = JSON.stringify(object);

    let xhr = new XMLHttpRequest();
    let url = '/loginform';
    xhr.open('POST', url, true);

    //Send the proper header information along with the request
    xhr.setRequestHeader('Content-type', 'application/json');
    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4 && xhr.status === 200) {
            try {
                console.log(xhr.responseText);
                document.getElementById('loginForm').reset();

                // if logged in
                if (xhr.responseText === LOGIN_SUCCESS) {
                    console.log('logged in!');
                    sessionStorage.setItem('display-message', 'loggedin');
                    document.getElementById('login-failed').classList.add('displayNone');
                    window.location = '/';
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