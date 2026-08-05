const toast = {
  success: (message) => {
    const container = document.getElementById('toast-container') || createToastContainer();
    const toastEl = document.createElement('div');
    toastEl.className = 'toast toast-success';
    toastEl.textContent = message;
    container.appendChild(toastEl);
    setTimeout(() => toastEl.remove(), 4000);
  },
  error: (message) => {
    const container = document.getElementById('toast-container') || createToastContainer();
    const toastEl = document.createElement('div');
    toastEl.className = 'toast toast-error';
    toastEl.textContent = message;
    container.appendChild(toastEl);
    setTimeout(() => toastEl.remove(), 5000);
  },
  info: (message) => {
    const container = document.getElementById('toast-container') || createToastContainer();
    const toastEl = document.createElement('div');
    toastEl.className = 'toast toast-info';
    toastEl.textContent = message;
    container.appendChild(toastEl);
    setTimeout(() => toastEl.remove(), 4000);
  }
};

function createToastContainer() {
  const container = document.createElement('div');
  container.id = 'toast-container';
  container.className = 'toast-container';
  document.body.appendChild(container);
  return container;
}

export default toast;