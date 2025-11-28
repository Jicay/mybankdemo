const Auth = {
    get token() {
        return localStorage.getItem('jwt');
    },
    set token(t) {
        if (t) localStorage.setItem('jwt', t); else localStorage.removeItem('jwt');
    },
    get clientId() {
        return localStorage.getItem('clientId');
    },
    set clientId(c) {
        if (c) localStorage.setItem('clientId', c); else localStorage.removeItem('clientId');
    },
    get login() {
        return localStorage.getItem('login');
    },
    set login(l) {
        if (l) localStorage.setItem('login', l); else localStorage.removeItem('login');
    },
    logout() {
        try {
            localStorage.removeItem('jwt');
            localStorage.removeItem('clientId');
            localStorage.removeItem('login');
        } catch (_) {
        }
        // Redirect to login page
        window.location.href = '/login';
    }
};

async function fetchWithRedirect(url, options = {}, withRedirect = true) {
    try {
        const res = await fetch(url, options);
        if (!res.ok) {
            if (withRedirect) {
                // Decide where to redirect based on status
                const status = res.status;
                if (status === 401) {
                    // Keep current UX: redirect to login for unauthorized
                    window.location.href = '/login';
                } else if (status === 403) {
                    window.location.href = '/error/403';
                } else if (status === 404) {
                    window.location.href = '/error/404';
                } else if (status >= 500) {
                    window.location.href = '/error/500';
                } else {
                    // Fallback
                    window.location.href = '/error/generic';
                }
            } else {
                throw new Error('Error ' + res.status);
            }
        }
        return res.json();
    } catch (e) {
        if (withRedirect) {
            // Network error or CORS, show generic error page
            window.location.href = '/error/generic';
        }
        throw e;
    }
}

window.Auth = Auth;