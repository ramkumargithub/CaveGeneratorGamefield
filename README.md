# CaveGeneratorGamefield
CaveGenerator

**1 Introduction**

This is an offline test for applying to senior software developer in Bluefors. The task is considered to be confidential, so please don’t distribute it. The test is held in two parts. The first part is an offline exercise of implementing following piece of program. The second part is a review session where you must be prepared to present your solution.

1.1 Offline session First part is the offline session. You have been given this document when the task has been started and from that point on you have 3 hours to complete the task. You may use any data structures and libraries that can be considered to be built-in with the language of your choice, e.g. Java API, C standard library, etc. You may also seek information e.g. from Google, but please don’t use ready answers or implementations for any part of the program as we are not only grading the solution but also how you came up with it.

1.2 Review session The second part is a short review session where you shortly present your solution and explain how you came up with it. You may create a short presentation of 5-10 minutes or so, if you like, but it is not needed as we will also have set of questions prepared for you. Remember that all you do is part of your solution. Non-working solution with professional attitude, design and presentation is far better than the best solution from the internet without any explanations.

**2 The task: Volcanic excursion**

2.1 Introduction You are a geologist fallen into an old lava cave. The cave itself is now empty, but there are lava streams underneath. While mostly being stable, some parts of the floor are very volatile and walking cane as your only friend you try to find your way out.

2.2 The task The goal of the game is to get from start of the game area to finish without falling through the collapsing floor. Your task is to write an algorithm to generate a play field to a simple game that represents the cave. No other parts of the game (solving or gameplay) needs to be implemented, only the play field algorithm and required code to execute it and show the result. You are free to use language or tools of your choice1

2.3 Play field structure The play field is essentially a rectangular area of characters where each character has a special meaning. 1Although, one must be prepared for demonstration and more detailed presentation if it is less common, i.e. other than C, C++, Java, Scala, Kotlin, Python, etc.

The play field contains impassable walls and volatile floor areas that breaks and kills instantly. To avoid falling through the floor, the player has a walking cane that can be used for knocking the floor. Around each breaking area tapping the floor produces hollow sound as an early warning. Start, finish, walls and collapsing points must be placed in empty squares and must not overlap with each other. It is sufficient to generate the play field according to given rules. It does not need to be playable, i.e. no need to check whether it is possible to feasibly find the path to finish. Recommended order of operations

Add start and finish
Add walls
Add collapsing spots
2.3.1 Empty Passable squares without a collapsing spot or a wall are marked as .. 2.3.2 Start and finish Start is marked as > and is positioned in random position on left edge of the play field. Finish is marked as < and is positioned in random position on right edge of the play field.

2.3.2.1 Example An empty play f i e l d with only s t a r t and f in i sh . . . . . . .

. . . . . . . . . . . . . . . . <

2.3.3 Walls Walls are marked as W. The playarea will have user specified amount of walls. The walls... ‚ are positioned randomly ‚ must fully fit to play field ‚ but may be right next to each other ‚ lengths varies randomly from 1 to 5 ‚ may be either horizontal or vertical

2.3.3.1 Example Part o f a play f i e l d with four randomly placed walls . . . . . . . . . . WWW . . . . . . . . . . . . . . . . W . . . . . . . W . W . . . . . . . . . . WWWW . . . 2.3.4 Sources of radiation Collapsing floor spots are marked as C. The hollowness of the floor is marked with numbers 1-9 The playarea will have user defined amount of collapsing spots.

Each spot is also surrounded by an empty space under the floor causing hollow sound when tapped. The area extends to adjacent squares to all directions, including diagonally, but only one square away. The sound hollowness in surrounding areas is increased by one. Each collapsible area nearby forms biggrer cavity and so produces more hollow sound, so each collapsible area in adjacent squares increases the hollowness by one. The collapsing spots should be places to the play field after start, finish and walls. The program must fail with an error if remaining space is not enough to occupy all collapsing spots.

2.3.4.1 Example Part of play field with two collapsing sports with connecting hollow area. . . . . . . . . 1 1 1 . . . . 1 C 2 1 1 . . 1 1 2 C 1 . . . . 1 1 1 . . . . . . . .

2.4 User interface The software must have easy way of inputting parameters, e.g from command line. Parameters that user must supply ‚ Play field width ‚ Play field height ‚ Amount of walls ‚ Amount of collapsing spots

2.5 Example run $ java Cave . java 10 20 5 20 Creating a pl a y fi eld o f si ze 10x20 with 5 buildings and 20 collapsing spots 1 1 1 1 1 1 1 1 1 . 1 C 1 1 C 1 1 C 1 . 1 1 1 1 WWWWW . . . 1 C 1 1 C 1 . . . 1 2 2 1 1 1 1 . . . 1 C 1 WW 1 1 1 1 < 2 2 2 1 2 C 2 1 1 . 1 C 1 1 C 2 2 C 1 . 1 1 1 1 2 2 2 1 1 . . . 1 1 W C 1 . . 1 1 W 1 C W 1 1 . . 1 C 1 1 1 W 1 1 . . 1 1 1 . . 1 C 1 . > . . . . . 1 1 2 1 1 . 1 1 1 . . . 1 C 1 . 1 C W . 1 1 2 1 1 . 1 1 W . 1 C 1 . . 1 1 1 1 1 2 1 2 1 1 1 C 1 1 C 1 . 1 C 1 1 1 1 1 1 1 . 1 1 1

**3 Hints** ‚ The time goes fast, so it is better to start off by making minimum viable product before polishing up ‚ It may be a good idea to make intermediate results to see how it is working ‚ Don’t panic. The task may seem to be much harder than it really is ‚ Using an IDE may be helpful ‚ If some parts of the task with certain parameters would take too long time to complete, it is better to fail than lock-up. It should always work with reasonable set of parameters. ‚ Don’t hesitate to ask if something is unclear ‚ Features that does not need to be implemented can be implemented as a bonus, if solution is already otherwise working. ‚ Don’t be late

Write the code in Java. Performance efficient and well architectured
