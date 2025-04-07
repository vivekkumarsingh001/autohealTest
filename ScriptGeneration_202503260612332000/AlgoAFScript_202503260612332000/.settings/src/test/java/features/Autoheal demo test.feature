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
And I clicked Computing and Internet in demo web shop books
And I selected Add to cart in demo web shop computing and internet
Then verify text The product has been added to your in demo web shop computing and internet
When I clicked Shopping cart in demo web shop computing and internet
And I checked removefromcart in demo web shop shopping cart
And I selected updatecart in demo web shop shopping cart
Then verify text Your Shopping Cart is empty in demo web shop shopping cart
And '<page>' is displayed with '<content>'

Examples:
|SlNo.|Email|Password|page|content|
|1|qwerty75@gmail.com|qwerty|Demo Web Shop Shopping Cart|NA|

#Total No. of Test Cases : 1

