import time
import math
import pymysql
from bs4 import BeautifulSoup
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

main_url = "http://www.gunsys.com/q/index.php?currentPage={}&bigCode=10&midCode=1010&smallCode="
driver = webdriver.Chrome("C:/driver/chromedriver.exe")

try:
    for page in range(1,4):    # page 1 ~ 4
        driver.get("http://www.gunsys.com/q/index.php?currentPage={}&bigCode=10&midCode=1010&smallCode=".format(page))

        for test in range(2, 22): # 한 페이지마다 문제풀기 클릭 
            test_num = driver.find_element_by_css_selector("#body_style > div > div > div > div > table:nth-child(2) > tbody > tr:nth-child({}) > td:nth-child(5) > a".format(test)).click()

            for test2 in range(2, 7): # 과목 선택 1과목 ~ 5과목
                subj_btn = driver.find_element_by_xpath('//*[@id="index_div"]/table/tbody/tr[3]/td/table/tbody/tr[{}]/td[4]/a'.format(test2)).click()

                for test3 in range(0, 4): # 0,1,2,3, 10,11,12,13, ...
                    
                    for test4 in range(1, 6): # 1,2,3 -> 왼 4,5 -> 오
                        if test4 < 4:    # 1,2,3 -> 왼
                            test_class1_num1 = driver.find_element_by_xpath('//*[@id="div{}{}"]/table[1]/tbody/tr/td[1]/table[{}]/tbody/tr[1]/td[1]'.format(test2-2,test3,test4))
                            test_class1_exam1 = driver.find_element_by_xpath('//*[@id="div{}{}"]/table[1]/tbody/tr/td[1]/table[{}]/tbody/tr[1]/td[2]'.format(test2-2,test3,test4))
                            try:
                                test_class1_img1 = driver.find_element_by_xpath('//*[@id="div{}{}"]/table[1]/tbody/tr/td[1]/table[{}]/tbody/tr[1]/td[2]/img'.format(test2-2,test3,test4))
                            except Exception as e1_img :
                                print("없어용1")

                        else:
                            test_class1_num1 = driver.find_element_by_xpath('//*[@id="div{}{}"]/table[1]/tbody/tr/td[2]/table[{}]/tbody/tr[1]/td[1]'.format(test2-2,test3,test4-3))
                            test_class1_exam1 = driver.find_element_by_xpath('//*[@id="div{}{}"]/table[1]/tbody/tr/td[2]/table[{}]/tbody/tr[1]/td[2]'.format(test2-2,test3,test4-3))
                            try:
                                test_class1_img1 = driver.find_element_by_xpath('//*[@id="div{}{}"]/table[1]/tbody/tr/td[2]/table[{}]/tbody/tr[1]/td[2]/img'.format(test2-2,test3,test4-3))
                            except Exception as e1_img :
                                print("없어용2")

                        str_test_class1_num1 = test_class1_num1.text
                        str_test_class1_exam1 = test_class1_exam1.text
                        print(str_test_class1_exam1)
                        print(str_test_class1_num1, str_test_class1_exam1,test_class1_img1)
                            
                    if(test3 != 3):    # 마지막페이지 다음 누르면 에러뜨기 때문
                        next_btn = driver.find_element_by_xpath('//*[@id="div{}{}"]/table[2]/tbody/tr/td[2]/input'.format(test2-2,test3)).click()

                next_btn = driver.find_element_by_xpath('//*[@id="body_style"]/div/div/div/div[2]/table[1]/tbody/tr/td[2]/input').click()


            time.sleep(5)
            print("-----------------------",test,"-------------------")
            driver.back()

except Exception as e1 :
    print("e1=============", e1)
finally:
    driver.close()

# 수정 합니다!
# 수정 -대훈-
# 수정--------------------------------------