import logging


class HasLoggerFilter(logging.Filter):
    def filter(self, record):
        return hasattr(record, 'funcName') and 'logger' in record.funcName


views_logger = logging.getLogger(__name__)

handler = logging.FileHandler('logs/views.log')

formatter = logging.Formatter('%(asctime)s - %(name)s - %(levelname)s - %(message)s')

handler.addFilter(HasLoggerFilter())

handler.setFormatter(formatter)

views_logger.addHandler(handler)
