# Installation
You need to have Python3 installed on your computer to run the server.

## Windows
For activation of the virtual environment from your current directory, run the commands:
```
python -m venv venv
.\venv\Scripts\activate
```
This will enable local installation of all of the dependencies.

If the terminal returns an error **'cannot be loaded because running scripts is disabled on this system'**,
then open the Windows PowerShell as an administrator and run the following command:
```
Set-ExecutionPolicy RemoteSigned
```
The OS will then allow the script to run and you will be able to install the virtual environment normally.

## Ubuntu
For activation of the virtual environment from your current directory, run the commands:
```
virtualenv --python python3 venv
source venv/bin/actuvate
```

## Dependencies

The API runs on the following dependencies:
- Django:
     - Django server,
     - Django REST Framework,
     - Django CORS Headers,
- mongoDB libraries:
     - PyMongo,
     - MongoEngine,
     - Django MongoEngine,
- requests,
- BCrypt,
- Python DotEnv,
- FlightRadar API.

For the installation of all of the needed dependencies on the virtual environment, run the following command:
```
pip install -r dependencies.txt
```

## Starting the server

To start the server, use the following command:
```
python manage.py runserver
```

or just run the server by using the following command in terminal (WINDOWS ONLY):
```
python start.py
```
which will create a virtual environment for the user, download all of the dependencies and lastly, it will start the server.
