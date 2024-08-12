<h1 id="design-overview">Design Overview</h1>
<p>The design centres around a directed graph of intersections and
roads, these intersections have signals and different directions for a
vehicle to take. The vehicles drive along these roads</p>
<h2 id="game">Game</h2>
<p>The game engine is where everything comes together. Here we have the
main game loop called "update()" this method runs all the other methods
in this class. In this class we instantiate all the objects, we create
an array of vehicles, the player and load the map. Every loop, the main
update method, moves all the vehicles, changes the speed accordingly for
every vehicle, adds the vehicles to an intersection queue if they reach
one, remove the vehicles from the queue accordingly, set up random lane
changes for the vehicles, and test collisions. On timers we have the
light updates as we don’t want to change the lights every time step. The
player is also prompted every 3 time steps as well.</p>
<p>The Vehicle controller class is also another important class, it
contains a lot of important helper methods that are used in player and
the engine classes. It contains a mixture of random methods for the AI,
along with helper methods like "canMoveThrough" which determines if a
vehicle can go through an intersection or "laneClear" which determines
if a position on a road and lane is clear, this method is used a lot in
determining the vehicles speed (to not crash into).</p>
<h2 id="map">Map</h2>
<p>The Map package contains the Map class and a few other helper
classes. The map class contains an array of nodes (intersections), an
array of roads, the 2d intersection queue and a 2d array List of vehicle
locations. The intersection queue is used to hold the vehicles that
reach the intersection, this array is indexed by the intersection id,
The location Arraylist is used to make search time a lot faster by
keeping track of which vehicles are in each road, this array is based on
roads. One of the most important parts of the map class is the file
input system, this creates the directed graph from a file. The graph is
saved in a file called "DirectedGraph.tab" and by the name suggests is
in a tab format, it’s simple and allows for quick editing. This class
also has a lot of different useful helper methods, there are a few
private helper methods for the file input system and several public
methods that return directions and interact with the location
Arraylist.</p>
<h2 id="vehicle">Vehicle</h2>
<p>The vehicle package contains the class vehicle along with some child
classes and an enum. The vehicle class is abstract, and is inherited by
Car, Bus, and Truck. The vehicle class contains a lot of variables and
keeps track of a lot of things that have to do with the vehicles. The
vehicle class is mostly setters and getters, but it does have a
collision method.</p>
<h2 id="player">Player</h2>
<p>The player class is an extensive class that deals with all user I/O.
It starts with the prompt method that determines what kind of prompt to
give the player, it then goes to "surroundings", "changeLanes" or
"chooseDirection" depending on what the user wants to do. This method
also contains the "gamble" method that will do damage and lower
reputation depending if an action was successful.</p>
<h2 id="gui">Gui</h2>
<p>This package and class will render an image to the screen in future
parts. It will use data from the map and vehicle classes to do this.</p>
<h2 id="using-the-software">Using the software</h2>
<p>On load you will be provided with a prompt of some kind for
instance:</p>
<div class="spacing">
<pre><code>You&#39;ve reached an intersection, which direction would you like to go?
You can go: RIGHT(R) but the light is red Or WAIT(W)
R
Are you sure? The light is red (Y/N): Y
you won the gamble, your action was successful
Changed direction</code></pre>
</div>
<p>you can enter R to go right or W to wait, waiting is only useful if
the light is red. Going on a red light can sometimes be legal but it can
still be dangerous. The user is prompted if they are sure they want to
go on a red light, and then the dice are rolled. In this case we won the
dice roll and our action was successful, we then get a confirmation that
we change direction.</p>
<div class="spacing">
<pre><code>would you like to look at your surroundings? or change lanes? (S or C)
S
there is a car in front of you
there is a vehicle behind you
There is no left lane
your right is empty
</code></pre>
</div>
<p>Here we are prompted to look at our surroundings or change lanes. In
this case we decided to enter "S" to see our surroundings. We are then
given information about the vehicles around us.</p>
<div class="spacing">
<pre><code>would you like to look at your surroundings? or change lanes? (S or C)
C
There is no left lane
Which lane would you like to switch to? (L or R)
L
There is no left lane to switch to

R
there is a vehicle to your right, would you like to go anyway? (Y or N)
Y
you lost the gamble and damaged your car
Lane changed</code></pre>
</div>
<p>Here we are prompted to look at our surroundings or change lanes
again. In this case we decided to enter "C" to change lanes. Here we are
told we cannot go left as the lane doesn’t exist. If we still try to go
to the non existent lane was are told there is no lane to switch to. If
we chose to press R instead, here we see that there is already a vehicle
to our right, and we are asked if we want to go anyway, we roll the dice
and in this case we lost and damaged our car.</p>
<div class="spacing">
<pre><code>Vehicle: 2 collided with vehicle: 7. Vehicle 7 lost reputation</code></pre>
</div>
<p>As the simulation continues text like the one above show up from time
to time. This shows us that vehicle 2 and 7 unfortunately crashed into
each other and that Vehicle 7 was determined by the dice to be the one
at fault.</p>
