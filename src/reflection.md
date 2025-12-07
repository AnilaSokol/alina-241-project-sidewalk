1. Things that aren't working or inefficiencies
- Everything seems to be working, at least for the tests that I've tried. I didn't use a separate visited set like I've done in my labs, so some nodes probably get added to the queue more than once, which is a bit inefficient. I did write a test to make sure that it doesn't affect the functionality of the entire thing. Other than that, I think it runs as expected.

2. Important lessons
- I think that I understand how the algorithm functions much better now after implementing it.
- This project helped me get more comfortable with using graphs and writing tests for my code in Java.

3. Most challenging part
- When I was writing the tests, I kept forgetting to add the delta for the assertEquals and that really messed up my test.
- It took a bit of trial and error to set up the priority queue correctly. I added the NodeWithDistance as a way to keep track of the current best distance and provide a simple object for the pq to order.
- The algorithm itself was probably the most challenging. I understood the concept of how it works well, but implementing it was harder than I thought it would be. This is kinda connected to the previous point since pq is a big part of making it all work correctly.