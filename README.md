# Save Words
The purpose of this application was to help me learn Swedish words easier with my two favourite applications for language 
learning - [Drops](https://languagedrops.com/) and [Memrise](https://www.memrise.com/).

## The applications
Both applications are memorization applications but work a bit different. Drops is a visual tool, where you associate 
an image with the word in the language you are learning while Memrise is associating a word in a known language to a word 
you are trying to learn.

As I enjoy both I wanted to combine them. As Drops has amazing images and nicely grouped words to learn I was inclined to 
transfer them to Memrise and as such be able to associate both the image (the actual meaning of the word) and the word 
I am associating in my native language with it making the learning process faster.

Both Drops and Memrise have free and paid plans. 
The words I am saving from Drops are freely available under the "Visual Dictionary". Drops has as well topics that are 
available only to paying users. That's why I decided to make my Memrise course private.

My intention was to create a web application where I can add the root of the language or the category page I am trying to 
convert from one application to another. Currently the front end is not developed and I don't intend to add it anytime soon 
but will be added at a later time.

## Important properties
The next properties need to be set in order for the application to work. These include setting the language the words 
are saved from, specific HTML elements to identify the words, etc.

### Language properties
Default language to be downloaded. Should be changed based on wanted language and available paths.
```
default.link.starting.point=https://languagedrops.com/word/en/english/swedish/
default.link.core=https://languagedrops.com
default.language=swedish
default.folder.path=C:/Users/maria/Desktop/
```

### HTML elements
The names of the html element' classes where the needed information resides.

#### Language level
The page with all the categories and topics / lessons (Eg., "Food & Drinks category").
```
doc.language.category.container=category-container
doc.language.category.title=category-title-container
doc.language.category.topics=category-topics
doc.language.category.grid.column1=two-columns-grid-column1
doc.language.category.grid.column2=two-columns-grid-column2
doc.language.category.topic.info=linkable-word-box-container
```
#### Topic level
A page that displays words based on a topic, under a category (Eg., words under "Food" or "More Food").
```
doc.language.topic.row=topic-row
doc.language.topic.translated.word=topic-row-first-word
doc.language.topic.native.word=topic-row-second-word
doc.language.topic.illustration.for.word=topic-row-illustration
doc.language.topic.animation=animation-container
doc.language.topic.linkable.topics=linkable-words-container-word-container
```

#### Application properties
For the moment, I don't want the TOMCAT server to be started and for simplicity I don't want to remove it from the path.
```
spring.main.web-application-type=none
```

#### Logging
The logs should be checked to verify if there are any issues with some of the words.
```
logging.file.path=./logs
```
