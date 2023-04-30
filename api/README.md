# PYTHON API 

Za kreiranje virtualnega okolja poženite ukaza: 
```
python -m venv venv
.\venv\Scripts\activate
```
To bo omogočilo lokalno namestitev vseh odvisnosti.

**Windows**: Če terminal vrne napako 'cannot be loaded because running scripts is disabled on this system', odprite 
Windows PowerShell kot administrator in poženite naslednji ukaz:
```
Set-ExecutionPolicy RemoteSigned
```
Tako bo operacijski sistem dovolil pogon skripte, ki omogoča delo na virtualnem okolju.

Za namestitev vseh potrebnih odvisnosti na virtualno okolje poženite ukaz:

```
pip install -r dependencies.txt
```