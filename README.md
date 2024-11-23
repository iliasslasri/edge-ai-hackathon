# Intelligent Scheduling Assistant

This project demonstrates an AI-powered scheduling assistant that performs the following tasks:

1. **Generate Professional Appointment Requests**: Converts informal input into a polite and professional message using a language model.
2. **Text-to-Speech**: Converts the generated text into audio.
3. **Audio Recording**: Records responses from the recipient.
4. **Speech-to-Text Transcription**: Transcribes recorded audio to text.
5. **Response Extraction**: Extracts "Yes" or "No" responses from the transcription.

---

## How to Run

1. Install dependencies:
   ```bash
   pip install torch transformers torchaudio sounddevice soundfile whisperspeech
    ```
2. Run the script in the notebook.
3. Follow the instructions in the notebook to interact with the scheduling assistant.
4. The assistant will generate a professional appointment request, convert it to audio, call the recipient (doctor, etc), record the recipient's response, transcribe the response, and extract the response (Yes/No), and report it back to the user.

