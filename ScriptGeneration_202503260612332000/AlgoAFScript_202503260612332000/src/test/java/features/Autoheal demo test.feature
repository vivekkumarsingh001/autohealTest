Feature: Autoheal demo test1
#Regression Type
#Correct Values = true
#Incorrect Values = false
#Illegal Values = false
#Invalid Values = false
#Boundary Values = false
#Edge Cases Values = false

@Test_1
@uida2041643491
@set21
@test001
Scenario Outline: Test 
Given I have access to application
When I clicked Log in in demo web shop
And I entered Email in demo web shop login as '<Email>'
And I entered Password in demo web shop login as '<Password>'
And I selected Log in in demo web shop login
And I clicked Books2 in demo web shop


Examples:
|SlNo.|Email|Password|page|content|
|1|qwerty75@gmail.com|qwerty|Demo Web Shop Shopping Cart|NA|

#Total No. of Test Cases : 1

