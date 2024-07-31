const express = require('express');
const path = require('path');
const { execSync } = require('child_process');

const app = express();
const PORT = process.env.PORT || 3000;

app.use(express.static(path.join(__dirname, 'public')));
app.use(express.json());

app.post('/calculate', (req, res) => {
  const { binary1, binary2, roundingMode, digitsSupported } = req.body;

  let command = `java -cp bin src.Driver ${binary1} ${binary2} ${roundingMode}`;
  if (roundingMode === 'G') {
    command += ` ${digitsSupported}`;
  }

  try {
    const output = execSync(command).toString();

    const filteredOutput = filterRedundantLines(output, roundingMode);
    
    // write filtered output to a file
    const fs = require('fs');
    const filePath = path.join(__dirname, 'output.txt');
    fs.writeFileSync(filePath, filteredOutput);

    res.send(filteredOutput);
  } catch (error) {
    console.error('Error executing command:', error);
    res.status(500).send('An error occurred while processing the request.');
  }
});

// output file
app.get('/download', (req, res) => {
  const filePath = path.join(__dirname, 'output.txt');
  res.download(filePath, 'result.txt', (err) => {
    if (err) {
      console.error('Error sending file:', err);
      res.status(500).send('Error downloading file');
    }
  });
});

function filterRedundantLines(output, roundingMode) {
  const lines = output.split('\n');

  if (roundingMode === 'R') {
    return lines.slice(3).join('\n');
  } else {
    return output;
  }
}

app.listen(PORT, () => {
  console.log(`Server is running on http://localhost:${PORT}`);
});
