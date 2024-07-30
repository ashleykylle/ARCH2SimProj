form.addEventListener("submit", function(event) {
    event.preventDefault();
  
    const binary1 = document.getElementById("binary1").value;
    const binary2 = document.getElementById("binary2").value;
    const roundingMode = document.getElementById("roundingMode").value;
    let digitsSupported = 0;
  
    if (roundingMode === "G") {
      digitsSupported = document.getElementById("digitsSupported").value;
    }
  
    fetch("/calculate", {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({
        binary1,
        binary2,
        roundingMode,
        digitsSupported
      })
    })
    .then(response => response.text())
    .then(result => {
      resultBox.textContent = result;
      downloadButton.style.display = "block";
      downloadButton.addEventListener("click", function() {
        const text = resultBox.textContent;
        const blob = new Blob([text], { type: "text/plain" });
        const link = document.createElement("a");
        link.href = URL.createObjectURL(blob);
        link.download = "output.txt";
        link.click();
      });
    })
    .catch(error => {
      resultBox.textContent = "Error: Unable to connect to server.";
    });
  });

  app.post("/calculate", (req, res) => {
    console.log("Received calculation request:", req.body);
    // ...
  });