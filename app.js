// required dependencies
const express = require('express');
const path = require('path');
const { execSync } = require('child_process');

const app = express();
const PORT = 3000;

app.use(express.static(path.join(__dirname, 'public')));
app.use(express.json());

app.post('/calculate', (req, res) => {
  const { binary1, binary2, roundingMode, digitsSupported } = req.body;

  let command = `java -cp bin src.Driver ${binary1} ${binary2} ${roundingMode}`;
  if (roundingMode == 'G') {
    command += ` ${digitsSupported}`;
  }

  try {
    const output = execSync(command).toString();

    const filteredOutput = filterRedundantLines(output, roundingMode);
    
    res.send(filteredOutput);
  } catch (error) {
    console.error('Error executing command:', error);
    res.status(500).send('An error occurred while processing the request.');
  }
});

function filterRedundantLines(output, roundingMode) {
  // Split the output into lines
  const lines = output.split('\n');

  if (roundingMode === 'R') {
    // If rounding mode is 'R', remove the first three rows
    return lines.slice(3).join('\n');
  } else {
    // Otherwise, return the output as is
    return output;
  }
}


app.listen(PORT, () => {
  console.log(`Server is running on http://localhost:${PORT}`);
});