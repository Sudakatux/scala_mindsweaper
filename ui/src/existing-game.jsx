import React,{useEffect,useState} from 'react';
import { useParams } from 'react-router-dom';
import {splitEvery, isEmpty} from 'ramda'
import {fetchForGame, touchACell, flagACell} from './api'

const EmptyCell = () => (<div className="cell empty-cell"/>)
const AdjacentCell = ({amount}) => (<div className="cell adjacent-cell">{amount}</div>)
const UnknownCell = ({onClick}) => (<div onClick={onClick} className="cell unknown-cell"/>)
const Bomb = ({onClick}) => (<div onClick={onClick} className="cell bomb-cell"/>)
const Flagged = ({onClick}) => (<div onClick={onClick} className="cell flag-cell"/>)

const colManager = (playCell) => (row,colIdx) =>(
    <div className="game-row">
        {row.map(({cellType,display},rowIdx)=>{
            switch (cellType) {
                case 'Empty':
                 return <EmptyCell key={`${colIdx}_${rowIdx}`}/>;
                case 'Adjacent':
                  return <AdjacentCell key={`${colIdx}_${rowIdx}`} amount={display}/>
                case 'Bomb':
                  return <Bomb key={`${colIdx}_${rowIdx}`} />
                case 'Flagged':
                  return <Flagged key={`${colIdx}_${rowIdx}`} onClick={playCell(rowIdx,colIdx)} />
                default:
                  return <UnknownCell onClick={playCell(rowIdx,colIdx)} />;
            }
            }
        )}
    </div>
)


const playOnCellEffect = (name, stateUpdateEffect) => 
                        (row,col) => 
                        (event) => {
                            event.stopPropagation();
                            const action = event.metaKey ? flagACell : touchACell
                            action(name,row,col).then(json => stateUpdateEffect(json))
                        }
                        

export const ExistingGame = () => {
    const {name} = useParams();
    const [state,setState] = useState({board:[],rowCount:0,name:'',gameState:''});
    useEffect(() => {
        const fetchData = async () => {
         const data =  await fetchForGame(name);
         setState(data);
        }
        fetchData();
    },[name]);

    const { board=[], rowCount=1, gameState } = state;
    
    if(isEmpty(board)){
        return <div>Loading the best game ever...</div>;
    }
    
    const partitionByRowCount = splitEvery(rowCount,board);
    const renderCol = colManager(playOnCellEffect(name,setState));
    
    return (
     <div className="game-container">
         <div><h2>{name}</h2></div>
        <div>{gameState}</div>
        <div className="board-container">
            {partitionByRowCount.map(renderCol)}
        </div>
    </div>
    )
}