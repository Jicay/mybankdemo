async function fetchWithRedirect(url, options = {}, withRedirect = true) {
    try {
        const res = await fetch(url, options);
        if (!res.ok) {
            if (withRedirect) {
                // Decide where to redirect based on status
                const status = res.status;
                if (status === 404) {
                    window.location.href = '/error/404';
                }
                if (status >= 500) {
                    window.location.href = '/error/500';
                }
                // Fallback
                window.location.href = '/error/generic';
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
