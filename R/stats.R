library(stats)

data <- read.csv("false-analysis.csv")

# Comparisons to baseline
for (i in 3:ncol(data)) {
  htest <- mcnemar.test(data[,2], data[,i], correct=FALSE)
  print(htest)
}

# Pre x Self
mcnemar.test(data[,5], data[,6], correct=FALSE)
mcnemar.test(data[,7], data[,8], correct=FALSE)