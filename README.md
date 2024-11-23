# This was developed in the hackathon time, but was not pitched (this repo contains working project that was the first idea we had before going for another idea)

# Intelligent On-Device Scheduling Assistant

This project consists of two key components:

### 1. Android App for Automated Calling
This Android app allows users to select an audio file and make a call with the selected audio playing during the call. However, there is a current limitation: the audio volume is quite low and difficult to hear during the call. We need a workaround, such as using another device to play the audio during the call.

### 2. On-Device Inference Models
The second component involves inference models that can be deployed on a device to implement a portable, edge-based calling assistant, enabling smart functionalities without relying on cloud services.


> **Note**: The main code for this project can be found in the `scheduling_assistant.ipynb` file.

---

## Project Overview

The **Intelligent On-Device Scheduling Assistant** is an AI-powered assistant designed to work entirely on the device, ensuring privacy by avoiding reliance on cloud services. It provides a seamless experience for scheduling appointments with the following features:

1. **Generate Professional Appointment Requests**: Converts informal input into a polite and professional message using a language model.
2. **Text-to-Speech Conversion**: Converts the generated text message into audio.
3. **Audio Recording**: Records the response from the call recipient.
4. **Speech-to-Text Transcription**: Transcribes recorded audio responses to text.
5. **Response Extraction**: Extracts key responses ("Yes" or "No") from the transcription.

These features make it an ideal on-device solution for scheduling important meetings, such as medical appointments, without sharing your private information with cloud-based services.

---

## How to Run the Project

### Prerequisites

Ensure that you have the following dependencies installed:

- Python 3.x
- The required packages can be installed with the following command:

   ```bash
   pip install torch transformers torchaudio sounddevice soundfile whisperspeech
   ```

### Running the Scheduling Assistant

1. Open the `scheduling_assistant.ipynb` Jupyter notebook.
2. Run the script in the notebook by following the step-by-step instructions.
3. The assistant will:
   - Generate a professional appointment request from informal input.
   - Convert the generated text into audio.
   - Place a call (to a doctor, for example) and play the audio.
   - Record the recipient's response.
   - Transcribe the response and extract the key decision (Yes/No).
   - Report the response back to you.

---

## Limitations

- **Audio Quality During Calls**: The current implementation has limitations regarding audio volume, making it difficult to hear the played audio during a call. We are working on a solution to improve the experience, such as connecting a secondary device to assist with audio playback.

---

## Contributions

Contributions to enhance the assistant's functionality or resolve the current limitations are welcome. Feel free to fork the repository and create a pull request with your improvements.

---

## License

This project is licensed under the MIT License. Please see the `LICENSE` file for more details.

