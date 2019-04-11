# SubsetSumService
This application is meant to be a (limited) general-purpose solution to the Subset Sum problem.  Having built the back-end
service previously, the hope is to build a decent front-end that will allow users of any technical background to understand
and appreciate the complexity of this particular problem.

For now, the constraints are simple: the user provides a list of any positive and negative integers they desire.  The user
also provides a target number for which they wish to find summations (i.e. a list containing the values [1,2,3,4,5] with the
target sum of [7]).  In return, the application will spit out a list of all subsets that sum up to the desired target value
(in the previous example, these would be {[5,2], [4,3]}).

Due to the algorithm used, this application has certain limitations.  Given a list containing X positive integers and Y
negative integers, the application can't handle lists for which X * Y > 144.  Lists that surpass this limit run the risk of
resulting in HTTP Request Timeouts.  For reference, a list I generated where X = 12 and Y = 10 generated a response JSON of
about 1.9MB, consisting of over 75,000 lines of text.

# How To Use This Application

At present, the front-end GUI is nonfunctional.  The hope is to eventually provide a simple interface through which casual
users could interact with this service.  For the time being, though, it works exclusively through HTTP calls.

First, this application must be installed locally and set running via IDE of your choice.

Once this is handled, the local machine will then be able to accept HTTP calls.  Using any kind of HTTP request tool (such
as Postman), send a POST request to "localhost:9001/subsetSum".  An example JSON body is given in the project's root
directory.  Simply sending a properly formatted POST request will result in a response JSON consisting of all the possible
summations that meet the requirements set.
