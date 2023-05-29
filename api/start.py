import os
import sys


def is_running_in_venv():
    return (hasattr(sys, 'real_prefix')) or \
            (hasattr(sys, 'base_prefix') and sys.base_prefix != sys.prefix)


def install_dependencies():
    os.system('pip install -r .\\dependencies.txt')


def start_server():
    # check if a virtual environment is not yet installed
    if not os.path.isdir('venv/'):
        os.system('python -m venv venv')

    if not is_running_in_venv():
        os.system(".\\venv\\Scripts\\activate")

    install_dependencies()

    os.system('python manage.py runserver')


def main():
    start_server()


if __name__ == '__main__':
    main()
