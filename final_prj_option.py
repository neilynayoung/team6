import time
import math
import pymysql
from bs4 import BeautifulSoup
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

test_index = 0
main_url = "http://www.gunsys.com/q/index.php?currentPage={}&bigCode=10&midCode=1010&smallCode="
driver = webdriver.Chrome("C:/driver/chromedriver.exe")
try:
    for page in range(1,4):    # page 1 ~ 4
        driver.get("http://www.gunsys.com/q/index.php?currentPage={}&bigCode=10&midCode=1010&smallCode=".format(page))

        for test in range(17, 22): # 한 페이지마다 문제풀기 클릭 
            try:
                element = WebDriverWait(driver, 20).until(EC.presence_of_all_elements_located((By.CLASS_NAME, "td_border_common_qpass")))
            except Exception as e :
                print("대기 중 에러",e)

            test_num = driver.find_element_by_xpath('//*[@id="body_style"]/div/div/div/div/table[1]/tbody/tr[{}]/td[5]/a'.format(test)).click()
            current_url = driver.current_url    # 현재 url주소 
            yearhoi = driver.find_element_by_xpath('//*[@id="body_style"]/div/div/div/div[2]/table[1]/tbody/tr/td[1]').text    # 정보처리기사 필기 (2018년 2회 기출문제) 응시 Timer 0분 2초

            year1 = yearhoi.split(" (")
            year2 = year1[1].split("년")
            year = year2[0]

            hoi1 = yearhoi.split("년")
            hoi2 = hoi1[1].split("회")
            hoi = hoi2[0]

            for test2 in range(2, 7): # 과목 선택 1과목 ~ 5과목, 응시하기
                try:
                    element = WebDriverWait(driver, 20).until(EC.presence_of_all_elements_located((By.CLASS_NAME, "td_border_common_qpass")))
                except Exception as e :
                    print("대기 중 에러",e)
                subj_btn = driver.find_element_by_xpath('//*[@id="index_div"]/table/tbody/tr[3]/td/table/tbody/tr[{}]/td[4]/a'.format(test2)).click()
                driver.find_element_by_xpath('//*[@id="btnFinish{}"]/input'.format(test2-2)).click()
                alert = driver.switch_to_alert()
                alert.accept()
                driver.find_element_by_xpath('//*[@id="index_div"]/table/tbody/tr[3]/td/table/tbody/tr[{}]/td[4]/a[1]'.format(test2)).click()
                try:
                        element = WebDriverWait(driver, 20).until(EC.presence_of_all_elements_located((By.CLASS_NAME, "question01_qpass")))

                except Exception as e :
                    print("대기 중 에러",e)

                for test3 in range(0, 4): # 과목당 페이지 수 11~14, 21~24, 31~34, 41~44, 51~54 /
                    
                    for test4 in range(1, 6): #  한 페이지당 5문제! 4쪽

                        for test5 in range(1, 5): # 한 문제당 4문항
                            #print("    ",(test4+(test3*5)+((test2-2)*20)),test5)
                            op_num = test5 # 문항 번호
                            op_ans = 0


                            try:
                                element = WebDriverWait(driver, 20).until(EC.presence_of_all_elements_located((By.CLASS_NAME, "question01_qpass")))

                            except Exception as e :
                                print("대기 중 에러",e)
                            

                            str_op1 = driver.find_element_by_xpath('//*[@id="quesitem{0}{1}"]/a'.format((test4+(test3*5) + ((test2-2)*20)),test5))

                            str_op2 = driver.find_element_by_xpath('//*[@id="quesitem{0}{1}"]'.format((test4+(test3*5) + ((test2-2)*20)),test5))

                            answer_style = str_op2.get_attribute('style')

                            if(len(answer_style) > 2):
                                op_ans = 1
                            else:
                                op_ans = 0
                           
                            str_op = str_op1.text     # 선지 내용

                            num = test4 + (test3 * 5) + ((test2-2)*20)

                            if(int(num) / 20 <= 1):
                                sub = 1
                            elif(int(num) / 40 <= 1):
                                sub = 2
                            elif(int(num) / 60 <= 1):
                                sub = 3
                            elif(int(num) / 80 <= 1):
                                sub = 4
                            else:
                                sub = 5
                                
                            try:
                                test_check_img = driver.find_element_by_xpath('//*[@id="quesitem{0}{1}"]/a/img'.format((test4+(test3*5) + ((test2-2)*20)),test5)).text
                                check_img = 1

                            except:
                                check_img = 0
                                pass

                            #년 월
                            print(num, str_op, year, hoi, sub, op_num, check_img, test_index, current_url, op_ans)
                    
                        test_index = test_index + 1
                    if(test3 != 3):    # 마지막페이지 다음 누르면 에러뜨기 때문
                        next_btn = driver.find_element_by_xpath('//*[@id="div{}{}"]/table[2]/tbody/tr/td[2]/input'.format(test2-2,test3)).click()

                next_btn = driver.find_element_by_xpath('//*[@id="body_style"]/div/div/div/div[2]/table[1]/tbody/tr/td[2]/input').click()

            print("-----------------------",test,"-------------------")
            driver.back()
            driver.back()
            driver.back()
            driver.back()
            driver.back()
            driver.back()

except Exception as e1 :
    print("e1=============", e1)
    pass
finally:
    driver.close()