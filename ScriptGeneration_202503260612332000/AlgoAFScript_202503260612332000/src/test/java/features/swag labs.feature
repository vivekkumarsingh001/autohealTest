Feature: swag labs1
#Regression Type
#Correct Values = true
#Incorrect Values = false
#Illegal Values = false
#Invalid Values = false
#Boundary Values = false
#Edge Cases Values = false

@Login
@uid538765773
@set21
@test001
Scenario Outline: Login
Given I have access to application
When I clicked Username in swag labs as '<Username1>'
And I clicked Password in swag labs as '<Password2>'
And I selected loginbutton in swag labs
Then '<page>' is displayed with '<content>'

Examples:
|SlNo.|Username1|Password2|page|content|
|1|Test|Test|Swag Labs|NA|

#Total No. of Test Cases : 1

