# Pityoulish Background

I've been teaching a course on _Distributed Systems_ once a year, for over a decade now. It's mostly lectures and theory, so I tried to add some practice in the form of two programming exercises. Both implement basically the same client-server functionality, one with socket connections and a binary protocol, the other with Java RMI. I provided the server component, students could write their own client, and then I'd link the server to a projector and let the students try out their clients.

The programming exercises are completely voluntary and do not affect the grades in any way. Most of the students would try the first exercise, but not all of them successfully. Of those that succeeded, some spent way too much time on it. So the number of students that would attempt the second programming exercise was... low. Despite my attempts to lower the workload with utility classes and better instructions, it remained low.

In 2015, I tried a different approach. Instead of asking the students to program a client, I provided one - from which I had removed a few carefully selected code lines. The students just had to test all available commands of the client and fix the holes. Exceptions pointed them directly to the missing code.
The first programming exercise was done at home and tried out in the classroom. I received feedback that the task was too simple, but might work as an in-class exercise. So I held the second programming exercise in class.

As it turned out, the in-class programming exercise was too complex for most of the students. But I learned a lot about the problems they were facing. Too much information to digest in the exercise description. A lack of proficiency in Java programming. Too much code with an unknown structure to wade through.
These problems, I believe, can be addressed. By an improved collection of programming exercises. With better instructions. Better descriptions of the available code. Better delivery from my side. Gradual skill build-up, with self-paced preparation at home and supervised problem-solving in class. And supplemental programming tasks for those who are interested.

This project, Pityoulish, is the new home of my programming exercises. I had a bunch of code to start with, but didn't want to just dump that here. Instead, I'm building an all-new set of programming exercises, while re-using parts of the old code where appropriate. As of December 2016, there is a Tutorial, and two classroom exercises replacing the old ones. I still got a long way to go regarding the delivery though. And plenty of [enhancements](https://github.com/pityoulish/origins/issues?q=is%3Aissue+is%3Aopen+label%3Aenhancement) in mind for the exercises.

I'm teaching Distributed Systems from October through December each year. You can expect slow progress until summer, and last-minute panic in September. The flurry winds down as I wrap up for the end of the year.

## History and Lessons Learned

### 2021

This year, I've had an idea for a different application, with more potential for discussing server-side architecture, various integrations to be considered, and the option of re-creating the debugging exercises as well.
I relaunched my lecture on Distributed Systems around this idea. With my students, I discussed architecture options for the new application. It is unclear when (or if) I will get around to implementing something. There wouldn't have been time for debugging exercises anyway.

The Pityoulish project is now officially retired.


### 2020

Feedback:
[Tutorial #115](https://github.com/pityoulish/origins/issues/115),
[Sockets #116](https://github.com/pityoulish/origins/issues/116),
[Java RMI #121](https://github.com/pityoulish/origins/issues/121)

This year, the Corona virus forced the classes online. I ran the server code [on Kubernetes](https://github.com/pityoulish/origins/issues/106) in the cloud, with [minor problems](https://github.com/pityoulish/origins/issues/117).
Instead of checking in the classroom how students progressed, I gave them some time for self-paced debugging, then demonstrated how to fix the next problem. This worked reasonably well. I suppose not all participants actually worked on the exercises, but that is to be expected &mdash; and their choice.

The [Richardson Maturity Model](https://martinfowler.com/articles/richardsonMaturityModel.html) and [12+ Factor Apps](https://content.pivotal.io/ebooks/beyond-the-12-factor-app) were first-class topics in the course this year. Unfortunately, the Message Board is not a good example for either.
There are only two addressable resources for the Richardson Maturity Model, namely the message board and the ticket issuer. Neither messages nor tickets are designed to be addressed individually. All server code is crammed into a single source repository and container image, which must run as a single process. That's the exact opposite of 12+ factors.

For the future, I intend to start all over. Define an application protocol which enables all levels of the Richardson Maturity Model. Implement server-side logic and different client-facing protocols as separate apps. Keep the source code for each app in a separate repository. Use asynchronous messaging between the apps on the server side, as recommended by the [Reactive Manifesto](https://www.reactivemanifesto.org/). Make the apps stateless, use a database service where necessary. And run everything on my own Kubernetes cluster, built from a few Raspberry Pis.

Those are long-term goals, of course. I won't be able to replace the Message Board exercises by next year. But [pityoulish](https://github.com/pityoulish) is a GitHub organization now, as a home for what's coming. All enhancement issues for the Message Board are closed. Enhancements will be implemented with the new codebase, or not at all.



### 2019

Feedback:
[Tutorial #84](https://github.com/pityoulish/origins/issues/84),
[Sockets #87](https://github.com/pityoulish/origins/issues/87),
[Java RMI #90](https://github.com/pityoulish/origins/issues/90)

The Tutorial is working quite well by now.
To familiarize the students with the Message Board protocol, we discussed
the design of an HTTP API for it. That generated interest and contributions,
I'm planning to repeat this in the years to come. In particular while I don't
have [an HTTP exercise](https://github.com/pityoulish/origins/issues/6).

The Sockets exercise and the first part of the Java RMI exercise went
reasonably well. The second part of the Java RMI exercise turned out to be
an unmitigated desaster. Not a single student managed to solve any of the
planted bugs, without direct instructions from me what to do. The feedback
was that nobody could see the bigger picture beyond the code challenge.

I'm not sure if this year's class was extra challenged because they had
little development background, or if there is a more fundamental problem
with that exercise. On the other hand, I want to cut back the time spent
on Java RMI and remote calls anyway. So I decided to skip the second part
of the Java RMI exercise in future years. The first part matches with the
functionality of the Sockets exercise, I'll keep that around for a while.
The idea is to replace it with
[a gRPC exercise](https://github.com/pityoulish/origins/issues/36)
at some point in time.


### 2018

Feedback:
[Tutorial #69](https://github.com/pityoulish/origins/issues/69),
[Sockets #70](https://github.com/pityoulish/origins/issues/70),
[Java RMI #78](https://github.com/pityoulish/origins/issues/78)

I discussed the general Message Board protocol one week in advance of the
first classroom session. That seems to be a good approach.
The rewritten Tutorial instructions also worked. Students reported about
15 minutes for the coding, with varying efforts for setting up their IDE.
But there were still some IDE problems during the classroom sessions.
For 2019, I've changed the setup section of the Tutorial.
Using the command line for compiling classes and building the JAR
becomes the default, an IDE can still be used for editing if desired.

Both classroom sessions were structured as two debugging parts,
with some theory before and a break after each part.
After the second break, I kept the server running while talking about
simple, unrelated, easy-listening topics. This gives the interested students
time to continue working on the exercise during breaks and until the end.
I'll keep this schedule.

I am no longer pursuing the idea of _finishing_ the exercises
during the classroom session. The faster students finish and can then
help other students, or work on the extra challenges in the exercises.
The slower students will not catch up with the faster ones. But it
isn't necessary to finish an exercise in order to grasp what I want to convey.
One of the students reported in the second part of the Java RMI session
that he had finally understood the point of the first part.
That's good enough :-)

We've had an additional plenum discussion about designing a REST API
for the Message Board, see [issue #6](https://github.com/pityoulish/origins/issues/6#issuecomment-429380717).
I don't know when or whether I will find time to implement this as
a third classroom exercise, but the discussion was interesting in itself.


### 2017

Feedback:
[Tutorial #56](https://github.com/pityoulish/origins/issues/56),
[Sockets #57](https://github.com/pityoulish/origins/issues/57),
[Java RMI #60](https://github.com/pityoulish/origins/issues/60)

Using the classroom exercises as topic introductions worked. The additional guidance from interleaving explanations and coding also proved helpful. I still have to work on the details, how much to present in advance and inbetween the coding phases.
Several students asked about the marker in the high-level API at the end of the second (!) classroom session.

I added an optional task to the RMI exercise, so that the faster programmers still have something to do. That worked quite well, so I've updated the Sockets exercise accordingly.
The linear Tutorial is too wordy for students who already know must of the stuff. Still, having a Tutorial is important.
The RMI exercise does not enough to introduce the concepts. Java has made remote calls too convenient. gRPC might be better for that purpose.

For 2018, I'm going to refactor the Tutorial instructions. The main page should be lean, for experienced programmers. Details will be provided on extra pages, for those who need or want them.
Exercise instructions must also become leaner. RMI got weeded out for this year, Sockets is prepared for next year.
Maybe I'll also find time to update Instructor's Notes with the interleaved presentation model.
Creating a new gRPC exercise will probably take longer.

Between the Tutorial assignment and the first classroom exercise, I'm planning an extra session to present the high-level API of the Message Board. This can also be used to discuss problems encountered during the Tutorial. Students can help eachother with their IDE problems.


### 2016

Feedback:
[Tutorial #37](https://github.com/pityoulish/origins/issues/37),
[Sockets #38](https://github.com/pityoulish/origins/issues/38),
[Java RMI #48](https://github.com/pityoulish/origins/issues/48)

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
I created the [Pityoulish](https://github.com/pityoulish/origins) project and wrote the Tutorial. It will help students to set up their development environment and learn the basic steps for debugging at home, before the first classroom exercise.

### before 2015
Program at home, try out in class. Some students tried the first exercise (sockets+TLV), many failed. Those that succeeded spent way more time than I had anticipated or intended. Few even tried the second exercise (RMI).
