Author: Samson Fagade
Date: Feb 10th, 2020

LEDAR Concept Application

Introduction:
	•	I originally developed this application back in 2016, I believe the first files were created in January of that year. That was my first project I was going to start from scratch as a team lead that would eventually make it to production. I used what was then the best most popular technologies in Java (or so I thought), and I went about developing the application.

Infrastructure:
	•	Java full stack application with an Android frontend, built using JDK 8 which was the popular version at that time. I built the backend using JAVA EE 6 (later upgraded to 7), the break down of the entire stack is as follows:

	1.	Backend:
	⁃	JBOSS Wildfly Server
	⁃	MySQL Database
	⁃	Packaging EAR file deployed on JBOSS
	⁃	EJB 3.0
	⁃	JPA/ Hibernate
	⁃	
	2.	Web Platform:
	⁃	JSF Framework
	⁃	Jquery Framework
	⁃	Java Servlets
	⁃	RestFul Services
	⁃	Apache Shiro (for authentication) 
	3.	Mobile Platform:
	⁃	Android Application (developed from another team member)

This application is still in use in the firm I was working in when I created it (sadly) and it’s hosted locally by the firm. Oh did I also mention that at that time we used SVN as our code sharing technology.


Crazy Idea:
	•	I wanted to see how solving the same problem I worked on back then would be different now knowing what I know today.


Proposed Solution:
	•	My plan is to break the application down into smaller pieces (micro-services) using Spring-boot (I’m a Java guy). I’m considering using Postgres database (my new love) and a React frontend. I recently completed a React course on Udemy and I think this will be a good opportunity to find out how much I’ve really learnt about the library. The spring-boot part is going to be the easiest part of this new plan, but we’ll see. 
	•	Of cause the first step is getting the old app to run locally. I’ve been able to build the application successfully with very little change (only the hibernate entity classes) and I’ll try to deploy it once I get JBOSS wildfly to successfully download on my PC. The new solution is going to be hosted in another git repository and I may not make that one public, but I’ll be glad to share per request. 
