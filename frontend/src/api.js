const API_BASE_URL = 'http://localhost:8080/api/monitors'

export async function getMonitors() {
    const response = await fetch(API_BASE_URL)

    if (!response.ok) {
        throw new Error('Failed to fetch monitors')
    }

    return response.json()
}

export async function createMonitor(monitor) {
    const response = await fetch(API_BASE_URL, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(monitor),
    })

    if (!response.ok) {
        throw new Error('Failed to create monitor')
    }

    return response.json()
}

export async function checkMonitor(id) {
    const response = await fetch(`${API_BASE_URL}/${id}/check`)

    if (!response.ok) {
        throw new Error('Failed to check price')
    }

    return response.text()
}

export async function getPriceHistory(id) {
    const response = await fetch(`${API_BASE_URL}/${id}/history`)

    if (!response.ok) {
        throw new Error('Failed to fetch price history')
    }

    return response.json()
}

export async function deleteMonitor(id) {
    const response = await fetch(`${API_BASE_URL}/${id}`, {
        method: 'DELETE',
    })

    if (!response.ok) {
        throw new Error('Failed to delete monitor')
    }

    return response.text()
}