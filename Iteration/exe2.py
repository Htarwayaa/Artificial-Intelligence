import mdp
m1 = mdp.make2DProblem()
m1.valueIteration()
m1.printValues()

m2 = mdp.make2DProblem()
m2.policyIteration()
m2.printActions()