# C195

Source code for C195 performance evaluation

## Forked Repository Collaboration Cheatsheet

### Forked Repository Setup and Configuration

For forked repository:

- `git remote add upstream https://github.com/fork_owner/c195.git`

### Making Forked Repository Current With Source Repository

For forked repository:

- Get changes from owner repository: `git fetch upstream`
- Merge changes: `git merge upstream/master`

### Making Changes in Forked Repository Available to Source Repository

For forked repository:

- Sync changes to forked repository: `git push origin master:master`
- [Create Pull Request](https://github.com/lerwine/C195/compare/master...fork_owner:master)

### Merging Changes from Forked Repository

- [Open Pull Request](https://github.com/lerwine/C195/pulls)
- Click on `Merge pull request`
- Click on `Confirm merge`
- Get changes from remote repository: `git fetch origin`
- Update local branch: `git pull --tags origin master`
