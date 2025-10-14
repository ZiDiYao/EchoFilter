import sys
import platform
import requests

def main():
    print("✅ Python environment test successful!")
    print(f"Python version: {sys.version}")
    print(f"Platform: {platform.system()} {platform.release()}")

    # Test network request
    response = requests.get("https://httpbin.org/get")
    if response.status_code == 200:
        print("🌐 Network request successful!")
        print("Response JSON example:", response.json()["url"])
    else:
        print("⚠️ Network request failed:", response.status_code)

if __name__ == "__main__":
    main()
