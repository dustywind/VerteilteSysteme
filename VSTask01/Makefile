
COMPILER = javac
RM = rm -rf

SRCDIR = src
BINDIR = bin

SRCFILES = $(SRCDIR)/vsue/rmi/*.java

all: ; \
    $(COMPILER) -sourcepath $(SRCDIR) -d $(BINDIR) $(SRCFILES)

clean: ; \
    $(RM) $(BINDIR)/*
    
