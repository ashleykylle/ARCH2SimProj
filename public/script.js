document.getElementById('calculator-form').addEventListener('submit', function(event) {
  event.preventDefault();

  const binary1 = document.getElementById('binary1').value;
  const binary2 = document.getElementById('binary2').value;
  const roundingMode = document.getElementById('roundingMode').value;
  let digitsSupported = 32; // defaults to 32

  if (roundingMode == 'G') {
    digitsSupported = document.getElementById('digitsSupported').value;
  }

  fetch('/calculate', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      binary1,
      binary2,
      roundingMode,
      digitsSupported
    })
  })
  .then(response => response.text())
  .then(data => {
    document.getElementById('result').textContent = data;
  })
  .catch(error => {
    console.error('Error:', error);
    document.getElementById('result').textContent = 'An error occurred';
  });
});

// pop up the supported digits when GRS rounding selected
document.getElementById('roundingMode').addEventListener('change', function() {
  const roundingMode = this.value;
  const digitsSupportedContainer = document.getElementById('digits-supported-container');

  digitsSupportedContainer.style.display = (roundingMode === 'G') ? 'block' : 'none';
});