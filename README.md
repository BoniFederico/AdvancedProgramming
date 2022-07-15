# java-custom-server

Project for the advanced programming course at University of Trieste. 
# Abstract

<p>Design and develop a server that, based on a text- and message-oriented protocol, takes requests of computation consisting of one or more mathematical expressions and input values and replies with the results.</p>

# Specification 

<h3 id="domain-definitions" class="wl">
  <span>Domain definitions</span>
 
</h3>

<p>Let $e$ be a <em>mathematical expression</em> composed of the binary operators $O={+,-,\times,\div,\text{pow}}$ and of zero or more named variables $V_e \in V$.</p>
<div class="alert alert-primary" role="alert">
  Example: with $e=\frac{x+1}{y-2^x}$, $V_e={x,y}$.
</div>

<p>Let $a: V \to \mathbb{R}^*$ be a <em>variable-values function</em> that associates a list of numerical values $a(v) \in \mathbb{R}^*$ with a variable $v$.</p>
<h3 id="protocol" class="wl">
  <span>Protocol</span>
 
</h3>

<p>Upon connection with a client $C$, the server $S$ performs iteratively these operations:</p>
<ol>
<li>waits for a <em>request</em> $r$</li>
<li>closes the connection or replies with a <em>response</em> $s$, depending on the content of $r$</li>
</ol>
<h4 id="request-format" class="wl">
  <span>Request format</span>
 
</h4>

<p>A request is a line of text with the following format (literal text is shown between double quotes <code>&quot;&quot;</code>, regexes between single quotes <code>''</code>):</p>
<pre tabindex="0"><code>Request = QuitRequest
        | StatRequest
        | ComputationRequest
</code></pre><p>The format of a <em>quit request</em> is:</p>
<pre tabindex="0"><code>QuitRequest = &quot;BYE&quot;
</code></pre><p>The format of a <em>stat request</em> is:</p>
<pre tabindex="0"><code>StatRequest = &quot;STAT_REQS&quot;
            | &quot;STAT_AVG_TIME&quot;
            | &quot;STAT_MAX_TIME&quot;
</code></pre><p>The format of a <em>computation request</em> is:</p>
<pre tabindex="0"><code>ComputationRequest = ComputationKind&quot;_&quot;ValuesKind&quot;;&quot;VariableValuesFunction&quot;;&quot;Expressions
</code></pre><pre tabindex="0"><code>ComputationKind = &quot;MIN&quot;
                | &quot;MAX&quot;
                | &quot;AVG&quot;
                | &quot;COUNT&quot;
</code></pre><pre tabindex="0"><code>ValuesKind = &quot;GRID&quot;
           | &quot;LIST&quot;
</code></pre><p>A variable-values function can be specified with the following format:</p>
<pre tabindex="0"><code>VariableValuesFunction = VariableValues
                       | VariableValuesFunction&quot;,&quot;VariableValues
</code></pre><pre tabindex="0"><code>VariableValues = VarName&quot;:&quot;JavaNum&quot;:&quot;JavaNum&quot;:&quot;JavaNum
</code></pre><pre tabindex="0"><code>VarName = '[a-z][a-z0-9]*'
</code></pre><p>and <code>JavaNum</code> is a string that can be correctly parsed to a <code>double</code> using the Java <code>Double.parseDouble()</code> method.
A list of expressions can be specified with the following format:</p>
<pre tabindex="0"><code>Expressions = Expression
            | Expressions&quot;;&quot;Expression
</code></pre><pre tabindex="0"><code>Expression = VarName
           | Num
           | &quot;(&quot;Expression&quot;&quot;Op&quot;&quot;Expression&quot;)&quot;
</code></pre><pre tabindex="0"><code>Num = '[0-9]+(\.[0-9]+)?'
</code></pre><pre tabindex="0"><code>Op = &quot;+&quot;
   | &quot;-&quot;
   | &quot;*&quot;
   | &quot;/&quot;
   | &quot;^&quot;
</code></pre><h5 id="examples" class="wl">
  <span>Examples</span>
  <a href="#examples" aria-label="Anchor">#</a>
  <a href="#content">↰</a>
</h5>

<p>Some examples of valid requests are (one per line):
<div class="alert alert-primary" role="alert">
  <p>Valid requests:</p>
<pre tabindex="0"><code>BYE
STAT_MAX_TIME
MAX_GRID;x0:-1:0.1:1,x1:-10:1:20;((x0+(2.0^x1))/(1-x0));(x1*x0)
COUNT_LIST;x0:1:0.001:100;x1
</code></pre>
</div>
</p>
<p>Some examples of <strong>not valid</strong> requests are:
<div class="alert alert-primary" role="alert">
  <p>Not valid requests:</p>
<pre tabindex="0"><code>bye
MIN_GRID;x0:-1:0.1:1,x1:-10:1:20;((x0+(2.0^x1))/(1-x0));log(x1*x0)
COUNT_LIST;x0:1:0.001:100;
MAX_LIST;x0:0:0,1:2;(x0+1)
</code></pre>
</div>
</p>
<h4 id="response-format" class="wl">
  <span>Response format</span>
  <a href="#response-format" aria-label="Anchor">#</a>
  <a href="#content">↰</a>
</h4>

<p>A response is a line of text with the following format:</p>
<pre tabindex="0"><code>Response = ErrorResponse
         | OkResponse
</code></pre><p>The format of an <em>error response</em> is:</p>
<pre tabindex="0"><code>ErrorResponse = ERR&quot;;&quot;'[^;]*'
</code></pre><p>The format of an <em>ok response</em> is:</p>
<pre tabindex="0"><code>OkResponse = OK&quot;;&quot;JavaNum&quot;;&quot;JavaNum
</code></pre><p>where <code>[^;]*</code> does not include new line characters.</p>
<h3 id="request-processing-specifications" class="wl">
  <span>Request processing specifications</span>
 
</h3>

<p>If the request $r$ is a <em>quit request</em>, the server $S$ must immediately close the connection with the client $C$.</p>
<p>Otherwise, $S$ must reply with a response $s$.
If $s$ is an error response, the part of $s$ following <code>ERR;</code> must be a human-comprehensible, succint textual description of the error.
Otherwise, if $s$ is an ok response, the first of two numbers following <code>OK;</code> must be the <em>response time</em>, i.e., the number of seconds $S$ took to process $r$, with at least 3 digits after the decimal separator (millisecond precision).</p>
<h4 id="stat-requests" class="wl">
  <span>Stat requests</span>
 
</h4>

<p>If $r$ is a stat request, $S$ replies with an ok response where the second number is:</p>
<ul>
<li>the number of ok responses served by $S$ (excluding $r$) to all clients since it started, if $r$ is <code>STAT_REQS</code>;</li>
<li>the average response time of all ok responses served by $S$ (excluding $r$) to all clients since it started, if $r$ is <code>STAT_AVG_TIME</code>;</li>
<li>the maximum response time of all ok responses served by $S$ (excluding $r$) to all clients since it started, if $r$ is <code>STAT_MAX_TIME</code>.</li>
</ul>
<h4 id="computation-requests" class="wl">
  <span>Computation requests</span>
 
</h4>

<p>If $r$ is a computation request, $S$ does the following steps:</p>
<ol>
<li>parse a variable-values function $a$ from the <code>VariableValuesFunction</code> part of $r$</li>
<li>build a list $T$ of <em>value tuples</em> from $a$, each value tuple specifying one value for each $v$ of the variables for which $a(v)\ne \emptyset$, depending on the <code>ValuesKind</code> part of $r$</li>
<li>parse a non-empty list $E = (e_1, \dots, e_n)$ of expressions from the <code>Expressions</code> part of $r$</li>
<li>compute a value $o$ on $T$ and $E$ depending on the <code>ComputationKind</code> part of $r$</li>
</ol>
<p>If any of the steps above fails, $S$ replies with an error response.
Otherwise $S$ replies with an ok response $s$ where the second number in $s$ is $o$.</p>
<h5 id="step-1-parsing-of-codevariablevaluesfunction/code-to-a" class="wl">
  <span>Step 1: parsing of <code>VariableValuesFunction</code> to $a$</span>
 
</h5>

<p>First, a list $I$ of tuples $(v, x_\text{lower}, x_\text{step}, x_\text{upper})$ is obtained by parsing each <code>VariableValues</code>.
If, for any tuple, $x_\text{step} \le 0$, the step fails.</p>
<p>Second, $a: V \to \mathcal{P}(\mathbb{R})$ is built as follows: if no tuple for $v$ exists in $I$, then $a(v)=\emptyset$; otherwise, $a(v)= (x_\text{lower}+k x_\text{step}: x_\text{lower}+k x_\text{step} \le x_\text{upper}){}_{k \in \mathbb{N}}$.</p>
<div class="alert alert-primary" role="alert">
  Example: <code>x0:-1:0.1:1,x1:-10:1:20</code> is parsed such that $a($<code>x0</code>$)=(-1,-0.9, \dots, 0.9,1)$, $a($<code>x1</code>$)=(-10,-9, \dots, 19,20)$, and $a(v)=\emptyset$ for any other $v$.
</div>

<h5 id="step-2-building-of-value-tuples-t-from-a" class="wl">
  <span>Step 2: building of value tuples $T$ from $a$</span>
 
</h5>

<p>If <code>ValuesKind</code> is <code>GRID</code>, than $T$ is the cartesian product of all the non empty lists in the image of $a$.</p>
<p>Otherwise, if <code>ValuesKind</code> is <code>LIST</code>, if the non empty lists in the image of $a$ do not have the same lenght, the step fails.
Otherwise, $T$ is the element-wise merging of those lists.</p>
<p>For example, for an $a$ parsed from <code>x:1:1:3,y:2:2:6</code>:</p>
<ul>
<li>$T=( (1,2), (2,2), (3,2), \dots, (1,6), (2,6), (3,6) )$ if <code>ValuesKind</code> is <code>GRID</code>;</li>
<li>$T=( (1,2), (2,4), (3,6) )$ if <code>ValuesKind</code> is <code>LIST</code>.</li>
</ul>
<p>where <code>x</code> and <code>y</code> are omitted in $T$ elements for brevity.</p>
<h5 id="step-3-parsing-of-codeexpressions/code-to-e" class="wl">
  <span>Step 3: parsing of <code>Expressions</code> to $E$</span>
 
</h5>

<p>For each <code>Expression</code> token in <code>Expressions</code>, an expression $e$ is built and added to $E$ by parsing the <code>Expression</code> token based on the corresponding context-free grammar.
If any of the expression parsing fails, the step fails.</p>
<p><strong>A sample code for performing this step is provided <a href="https://drive.google.com/drive/folders/1JOefnifnJYuqh21MTVWBl70kkzWz6qmA?usp=sharing">here</a> in the form of a few Java classes.</strong>
The student may freely get inspiration from or reuse this code.</p>
<h5 id="step-4-computation-of-o-from-t-and-e" class="wl">
  <span>Step 4: computation of $o$ from $T$ and $E$</span>
 
</h5>

<p>Let $V_t \in V$ be the set of variables for which a tuple $t$ defines the values and let $e(t) \in \mathbb{R}$ be the value of the expression $e$ for the variables values given by $t$ such that $V_t \supseteq V_e$.</p>
<p>Then:</p>
<ul>
<li>if <code>ComputationKind</code> is <code>MIN</code>, $o=\min_{e \in E, t \in T} e(t)$, or the step fails if $\exists e \in E: V_t \not\supseteq V_e$;</li>
<li>if <code>ComputationKind</code> is <code>MAX</code>, $o=\max_{e \in E, t \in T} e(t)$, or the step fails if $\exists e \in E: V_t \not\supseteq V_e$;</li>
<li>if <code>ComputationKind</code> is <code>AVG</code>, $o=\frac{1}{|T|} \sum_{t \in T} e_1(t)$, or the step fails if $V_t \not\supseteq V_{e_1}$;</li>
<li>if <code>ComputationKind</code> is <code>COUNT</code>, $o=|T|$.</li>
</ul>
<h3 id="examples-of-request-response-pairs" class="wl">
  <span>Examples of request-response pairs</span>
 
</h3>

<p>Some examples of request-response pairs:</p>
<div class="alert alert-primary" role="alert">
  <p>Request: <code>MAX_GRID;x0:-1:0.1:1,x1:-10:1:20;((x0+(2.0^x1))/(21.1-x0));(x1*x0)</code></p>
<p>Response: <code>OK;0.040;52168.009950</code></p>

</div>

<div class="alert alert-primary" role="alert">
  <p>Request: <code>COUNT_LIST;x0:1:0.001:100;x1</code></p>
<p>Response: <code>OK;0.070;99001.000000</code></p>

</div>

<div class="alert alert-primary" role="alert">
  <p>Request: <code>MIN_GRID;x0:-1:0.1:1,x1:-10:1:20;((x0+(2.0^x1))/(1-x0));log(x1*x0)</code></p>
<p>Response: <code>ERR;(ComputationException) Unvalued variable log</code></p>

</div>

<div class="alert alert-primary" role="alert">
  <p>Request: <code>STAT_MAX_TIME</code></p>
<p>Response: <code>OK;0.000;0.070000</code></p>

</div>

<h2 id="non-protocol-specifications" class="wl">
  <span>Non-protocol specifications</span>
 
</h2>

<p>The server must:</p>
<ul>
<li>log on the standard output or standard error significant runtime events as:
<ul>
<li>new connection from client</li>
<li>disconnection from client</li>
<li>errors</li>
</ul>
</li>
<li>listen on port $p$ specified as command-line argument</li>
<li>handle multiple clients at the same time</li>
<li>never terminate, regardless of clients behavior</li>
<li>at any time, do at most $n$ computation for processing computation requests at the same time, with $n$ being equal to the number of available processors on the machine where the server is running.
Note that the server must still be able to serve more than $n$ clients at the same time.</li>
</ul>
<p>Moreover, the server must:</p>
<ul>
<li>be a Java application delivered as a <code>.jar</code> named after the student last name and first name in upper camel case notation (e.g., <code>MedvetEric.jar</code>);</li>
<li>be executable with the following syntax <code>java -jar MedvetEric.jar </code>$p$ (e.g., <code>java -jar MedvetEric.jar 10000</code> for $p=10000$)</li>
</ul>
