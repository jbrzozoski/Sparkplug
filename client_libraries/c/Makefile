NAME     :=  sparkplug_b
SNAME    :=  lib/lib$(NAME).a
DNAME    :=  lib/lib$(NAME).so
SRC      :=  $(wildcard src/*.c)
OBJ      :=  $(SRC:.c=.o)
#CFLAGS   :=  -Wall -g3 -fPIC -Iinclude/
CFLAGS   :=  -g -g3 -fPIC -Iinclude/
LDFLAGS  :=  -L.
#LDLIBS  :=  -l$(...)

TEST     :=  test
TEST_OBJ := test/test.c
LD_TEST  := -Llib -L/usr/local/lib -l$(NAME)

.PHONY: all clean test re

all: $(SNAME) $(DNAME) $(TEST)

$(SNAME): $(OBJ)
	mkdir -p lib
	$(AR) $(ARFLAGS) $@ $^

$(DNAME): LDFLAGS += -shared
$(DNAME): $(OBJ)
	$(CC) $(LDFLAGS) -o $@ $^ $(LDLIBS)

$(TEST):
	$(CC) $(CFLAGS) -o test/test_static $(TEST_OBJ) $(SNAME) -lmosquitto
	$(CC) $(CFLAGS) $(LD_TEST) -o test/test_dynamic $(TEST_OBJ) -l$(NAME) -lmosquitto

clean:
	$(RM) $(OBJ)
	$(RM) $(SNAME) $(DNAME)
	$(RM) test/test_static test/test_dynamic

re: clean all
