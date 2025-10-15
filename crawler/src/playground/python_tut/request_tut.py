import requests
from requests.exceptions import Timeout

def fetch_data_with_timeout(url,seconds = 5):

    try:
        response = requests.get(url, timeout=seconds)
        response.raise_for_status()
        print(f"Get the data successfully {response.status_code}")
        return response.json()

    except Timeout:
        print(f"ERROR {url} excess {seconds}")
        return None

    except requests.exceptions.RequestException as e:
        print(f"Error {e}")
        return None


def try_exception():
    try:
        result = 10/0
    except ZeroDivisionError:
        print("Can not be zero")

    except Exception as e:
        print(f"ERROR {e}")