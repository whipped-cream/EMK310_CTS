# Proposal for the development of a new CTS

The current CTS system is written in a combination of PHP and Java 8. 
I (Michael Langley) developed a new AGS in Kotlin which is (I believe) far easier to write memos in.
The VM that the CTS is currently running on is years out of date. 

There are two options to rectify this and unify the two AGS versions:
1. Update the PHP code as required to work with the new AGS
2. Port the CTS to a newer language such as Kotlin

Clearly (1.) has the lower immediate maintenance burden, but PHP is a dying language, and as the new AGS is written in 
Kotlin, unification of the two projects is an attractive proposition. Two projects in a single language is likely less 
of a maintenance burden long term. As the existing AGS and CTS have existed for many years it is conceivable that a 
lower long-term maintenance burden could result in a significant time save. This project will develop a new CTS in Kotlin.
Kotlin is an appropriate choice of language due to its mature ecosystem of web-development frameworks and simple database 
interaction.

The proposed design is to use a backend process and a frontend process. The frontend process handles serving the page, 
accepting submissions, and passing jobs to the backend process. The backend process handles AGS related functionality 
and lifecycle of the MPLAB related objects. This was decided as, according to my testing MPLAB leaks memory either due 
to a fault in MPLABX or due to how we use it. Furthermore, separating vendor code (that we are hacking), from app logic 
is probably a good idea.

The two communicate over some protocol. For reasons that I don't exactly understand this is often HTTP. So either:
- REST
- WebSockets - May be required depending on how the two communicate. 

Putting this in a Docker (or Podman) container is certainly the 'correct' way to design such a system properly, the 
maintenance burden is more significant than running on bare metal. As this system is going to be maintained by engineers
making the code as simple to maintain as possible should be a priority.

## AGS Backend
The AGS needs a small wrapper that just talks to the AGS. Making this as lightweight as possible means that the process
can be restarted. The AGS backend should be stateless, ie:
1. Receives a marking job from the edge backend
2. Marks
3. Returns the resulting information

In other words, the AGS backend should defer things like queueing jobs to the edge backend.

## Edge Backend
### Toolkits
There are two major, mature(-ish) web-development frameworks for Kotlin:
1. Spring Boot: Mature, stable, not really Kotlin native, heavy
2. Ktor: Newer, more breaking changes, very Kotlin native, very lightweight

Obviously there are major benefits and drawbacks to choosing either. This project chooses Ktor, at least for now.

### Other functionality
The edge backend manages the AGS backend. Thus the edge backend will handle database updates, queuing, etc.

Recently a barcode scanner has been introduced as well for tracking attendance. The edge backend must serve this.

## Other notes
- TLS is terminated at the reverse proxy
- PostgreSQL is now more common than MySQL so we will use that
- Currently, passwords are stored in plaintext in the database. Instead, it would be preferable to hook into the SSO of the university and then validate the student number (TODO: how do email aliases 'michael.langley@tuks.co.za' vs 'u22663003@tuks.co.za' get affected)
- 