README

-------------------
Overview of Project
-------------------

This project combines several parts of datamining, Internet Of Things, and analytics to provide a source for usage of CSIL computers at UCSB's CoE lab.

It records who's logged into which computers when.  From there, you can run analytics on that data.  For example, you can see which computers are online and have the most/least users.  You can also see a timeline of who is logged into what computers.  Additionally, you can get a list of users currently logged into some computer.

Unsurprisingly, there was already a form of analytics for this provided by ECI http://www.engineering.ucsb.edu/eci/lab_status/.  In some ways, their page is actually a lot better.  It supports the Student Resource Center at Engineering 2 as well as (I believe) the Windows machines in HFH 1140.  However, their data is very general and doesn't provide much information about remote logins.  My application stores the raw analytics data.

-------------
Contributions
-------------

Google App Engine APIs -- Used as the framework for running my application

Gson -- Used for JSON serialization and Deserialization

Apache Commons Lang  -- Used for the String Escape Utility

-------------------
Novelty
-------------------

The reason why I chose to do this project was because:

As a Computer Science undergrad, I use CSIL quite frequently.  However, there are a lot of other undergraduate students who also use the labs.  Sometimes, the labs will be completely empty and sometimes, there aren't even enough chairs for everyone!  This made me wish that was some form of Internet of Things in CSIL.

Another thing I wanted was to see when my friends were in the CS labs so I can join them whenever to start working.

----------
Experience
----------

The quotas are harsh.  I initially blew through the frontend instance hours and had to aggregate those requests.

Then the datastore became increasingly slow because I had so much data.  Hence, I started to persist that information to the blobstore.

Database reads and writes were also very costly but I eventually used memcaches to optimize for that.  The only real issue remaining is the nightly backup process which basically requires us to completely wipe the database which is both a read and write process.

I have a sneeking suspicion that Google App Engine sometimes doesn't report errors that occur in JSPs.  Specifically, there were instances when I as testing as well as deploying where my code was not updating on the server, and it turned out that my code was actually fatally crashing.  The logs didn't say anything and my server would just show the most recent known working cached version of data.  I believe this only happens when there is a routing error though (like when you have a servlet and a jsp pointing towards the root directory).

----------------------
Performance Evaluation
----------------------



-------------------
Demonstrating App Execution
-------------------

-------
Testing
-------

Please see [TESTING.md](TESTING.md).
