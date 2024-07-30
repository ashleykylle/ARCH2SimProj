const express = require("express");
const app = express();
const path = require("path");
const childProcess = require("child_process");

app.use(express.json());
app.use(express.static("public"));

app.get("/", (req, res) => {
  res.sendFile(path.join(__dirname, "public", "index.html"));
});

app.post("/calculate", (req, res) => {
  const { binary1, binary2, roundingMode, digitsSupported } = req.body;

  const javaCommand = `java -cp bin src.Driver ${binary1} ${binary2} ${roundingMode} ${digitsSupported}`;

  childProcess.exec(javaCommand, (error, stdout, stderr) => {
    if (error) {
      console.error(`Error executing Java program: ${error}`);
      res.status(500).send("Error executing Java program.");
    } else {
      res.send(stdout);
    }
  });
});

app.listen(3000, () => {
  console.log("Server is running on http://localhost:3000");
});