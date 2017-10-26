# Pityoulish Background

I've been teaching a course on _Distributed Systems_ once a year, for over a decade now. It's mostly lectures and theory, so I tried to add some practice in the form of two programming exercises. Both implement basically the same client-server functionality, one with socket connections and a binary protocol, the other with Java RMI. I provided the server component, students could write their own client, and then I'd link the server to a projector and let the students try out their clients.

The programming exercises are completely voluntary and do not affect the grades in any way. Most of the students would try the first exercise, but not all of them successfully. Of those that succeeded, some spent way too much time on it. So the number of students that would attempt the second programming exercise was... low. Despite my attempts to lower the workload with utility classes and better instructions, it remained low.

In 2015, I tried a different approach. Instead of asking the students to program a client, I provided one - from which I had removed a few carefully selected code lines. The students just had to test all available commands of the client and fix the holes. Exceptions pointed them directly to the missing code.
The first programming exercise was done at home and tried out in the classroom. I received feedback that the task was too simple, but might work as an in-class exercise. So I held the second programming exercise in class.

As it turned out, the in-class programming exercise was too complex for most of the students. But I learned a lot about the problems they were facing. Too much information to digest in the exercise description. A lack of proficiency in Java programming. Too much code with an unknown structure to wade through.
These problems, I believe, can be addressed. By an improved collection of programming exercises. With better instructions. Better descriptions of the available code. Better delivery from my side. Gradual skill build-up, with self-paced preparation at home and supervised problem-solving in class. And supplemental programming tasks for those who are interested.

This project, Pityoulish, is the new home of my programming exercises. I had a bunch of code to start with, but didn't want to just dump that here. Instead, I'm building an all-new set of programming exercises, while re-using parts of the old code where appropriate. As of December 2016, there is a Tutorial, and two classroom exercises replacing the old ones. I still got a long way to go regarding the delivery though. And plenty of [enhancements](https://github.com/rolandweber/pityoulish/issues?q=is%3Aissue+is%3Aopen+label%3Aenhancement) in mind for the exercises.

I'm teaching Distributed Systems from October through December each year. You can expect slow progress until summer, and last-minute panic in September. The flurry winds down as I wrap up for the end of the year.

## History and Lessons Learned

### 2016

Feedback:
[Tutorial #37](https://github.com/rolandweber/pityoulish/issues/37),
[Sockets #38](https://github.com/rolandweber/pityoulish/issues/38),
[Java RMI #48](https://github.com/rolandweber/pityoulish/issues/48)

The tutorial worked as intended. Both classroom exercises went way over time. I relied on the written exercise instructions and background. Still too much information to digest. Some students don't even notice the method to copy from when it's right on their screen, just a few lines above the gap to fill in. Some notable feedback:
* A bit of hands-on is good.
* That's debugging, not programming.
* We didn't apply the stuff we learned before.
* We didn't get the big picture.

For 2017, I'm planning more guidance during the exercises. I'll lay out the big picture during the classroom exercise, interleaving my explanations with the programming/debugging tasks. Also, I intend to use the exercises more as introduction to the topics, rather than doing them afterwards.
It's true that students don't get to apply the stuff that I'm teaching. That's because I'm not teaching low-level programming details, and I don't want to. The exercises are meant as an illustration. I need to deliver them differently so that they can serve their purpose.

### 2015
I reworked the exercises to a fill-in-the-gaps style. Handed out instructions, a client with gaps, and a server to run at home.

First exercise, sockets+TLV: Home programming. Feedback from a few students: too easy, we wanted to do more. I asked whether the exercise would have been easy enough to do it in-class. Response: yes.

Second exercise, Java RMI: In-class. Groups of two, everybody tried, most failed. Many were unfamiliar with Java programming. Some didn't know how to interpret stack traces. Others couldn't get their IDE to generate an executable JAR. Some loaded the server JAR into the same IDE project as the client JAR, totally messing up the client-server boundary. Consequently, one group instantiated their own server-side objects instead of calling my server. Feedback: Still way too much information to digest during the exercise.

For 2016,
I created the [Pityoulish](https://github.com/rolandweber/pityoulish) project and wrote the Tutorial. It will help students to set up their development environment and learn the basic steps for debugging at home, before the first classroom exercise.

### before 2015
Program at home, try out in class. Some students tried the first exercise (sockets+TLV), many failed. Those that succeeded spent way more time than I had anticipated or intended. Few even tried the second exercise (RMI).
