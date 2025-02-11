import undetected_chromedriver as uc
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

# Use an undetected Chrome driver to bypass bot detection
options = uc.ChromeOptions()
options.add_argument("--headless")  # Runs Chrome in headless mode (remove this for debugging)
options.add_argument("--disable-gpu")
options.add_argument("--no-sandbox")

# Launch Chrome
driver = uc.Chrome(options=options)

# Open the website
url = "https://www.biblestudytools.com/csb/matthew/2.html"
driver.get(url)

# Wait until verses are loaded
try:
    WebDriverWait(driver, 15).until(
        EC.presence_of_element_located((By.CLASS_NAME, "verse"))
    )
    verses = driver.find_elements(By.CLASS_NAME, "verse")

    for verse in verses:
        print(verse.text.strip())

except Exception as e:
    print("Error: Could not find verses. The website structure may have changed.")

# Close browser
driver.quit()
