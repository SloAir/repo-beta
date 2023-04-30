import os
from dotenv import load_dotenv
from pymongo import MongoClient

load_dotenv('.env')

client = MongoClient(os.environ.get('DB_URI'))
db = client[os.environ.get('DB_NAME')]
print('Connected to the database!')
