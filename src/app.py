import sys
from groq import Groq
from dotenv import load_dotenv
import os

load_dotenv()

class Ai_assistant:
    def __init__(self):
        self.groq_client = Groq(api_key=os.environ["GROQ_API_KEY"])
        
    def generate_itinerary(self, prompt):
        system_prompt = """You are a travel planning assistant. Create a detailed day-by-day 
        itinerary based on the given parameters. Format your response as follows:
        
        **TRAVEL ITINERARY FOR [DESTINATION] from [ORIGIN]**
        
        **Duration:** [START DATE] to [END DATE]
        **Budget:** $[AMOUNT]
        
        **Day 1:**
        * Morning: [Activity]
        * Afternoon: [Activity]
        * Evening: [Activity]
        
        **Day 2:**
        * Morning: [Activity]
        [Continue for each day...]
        
        **Recommended Hotels:**
        * [Hotel 1]
        * [Hotel 2]
        
        **Tips:**
        * [Important tips]
        """
        
        messages = [
            {"role": "system", "content": system_prompt},
            {"role": "user", "content": prompt}
        ]
        
        chat_completion = self.groq_client.chat.completions.create(
            messages=messages,
            model="llama3-8b-8192"
        )
        return chat_completion.choices[0].message.content

if __name__ == "__main__":
    if len(sys.argv) > 1:
        assistant = Ai_assistant()
        prompt = sys.argv[1]
        print(assistant.generate_itinerary(prompt))