import React, {Component} from 'react';
import {
  BrowserRouter as Router,
  Switch,
  Route,
} from 'react-router-dom';

import {NewGame} from './new-game'
import {ExistingGame} from './existing-game'

import './App.css';

class App extends Component {
  render() {
    return (
      <Router>
        <Switch>
          <Route path="/game/:name" exact >
            <ExistingGame />
          </Route>
          <Route path="/" exact component={NewGame}/>
        </Switch>
      </Router>
    );
  }
}

export default App;
