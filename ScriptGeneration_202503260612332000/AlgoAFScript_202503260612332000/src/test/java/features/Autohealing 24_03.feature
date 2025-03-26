Feature: Autohealing 24_031
#Regression Type
#Correct Values = true
#Incorrect Values = false
#Illegal Values = false
#Invalid Values = false
#Boundary Values = false
#Edge Cases Values = false

@Verify_user_able_to_login_1
@uid415272106
@set21
@test001
Scenario Outline: Verify user able to login with valid credentials
Given I have access to application
When I entered Email in demo web shop as '<Email>'
And I entered Password in demo web shop as '<Password>'
And I selected Log in in demo web shop
Then '<page>' is displayed with '<content>'

Examples:
|SlNo.|Email|Password|page|content|
|1|Email1|Password1|Demo Web Shop|NA|

#Total No. of Test Cases : 1

