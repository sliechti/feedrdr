function initHello(fbKey, googKey, liveKey)
{
    hello.init({'google' : googKey, 'facebook' : fbKey, 'windows' : liveKey});
}

function setHelloCallbacks(onAuth, onLogin, onLogout)
{
    hello.on('auth', onAuth);
    hello.on('auth.login', onLogin);
    hello.on('auth.logout', onLogout);
}
