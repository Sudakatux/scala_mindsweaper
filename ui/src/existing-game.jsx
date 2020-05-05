import React from 'react';
import {splitEvery} from 'ramda'

const EmptyCell = () => (<div className="cell empty-cell"/>)
const AdjacentCell = ({amount}) => (<div className="cell adjacent-cell">{amount}</div>)
const UnknownCell = ({onClick}) => (<div onClick={onClick} className="cell unknown-cell"/>)
const Bomb = ({onClick}) => (<div onClick={onClick} className="cell bomb-cell"/>)

const renderRow = (row,rowidx) =>(
    <div className="game-row">
        {row.map(({cellType,display},idx)=>{
            switch (cellType) {
                case 'Empty':
                 return <EmptyCell key={`${rowidx}_${idx}`}/>;
                case 'Adjacent':
                  return <AdjacentCell key={`${rowidx}_${idx}`} amount={display}/>
                case 'Bomb':
                  return <Bomb key={`${rowidx}_${idx}`} />
                default:
                  return (<UnknownCell onClick={()=>console.log('Clicked')} />);
            }
            }
        )}
    </div>
)

export const ExistingGame = ()=>{
    const board = [
        {
          cellType: 'Bomb',
          display: 'NotVisible'
        },
        {
          cellType: 'Adjacent',
          display: '1'
        },
        {
          cellType: 'Empty',
          display: 'NotVisible'
        },
        {
          cellType: 'Empty',
          display: 'NotVisible'
        },
        {
          cellType: 'Adjacent',
          display: '1'
        },
        {
          cellType: 'Adjacent',
          display: '1'
        },
        {
          cellType: 'Empty',
          display: 'NotVisible'
        },
        {
          cellType: 'Empty',
          display: 'NotVisible'
        },
        {
          cellType: 'Empty',
          display: 'NotVisible'
        },
        {
          cellType: 'Empty',
          display: 'NotVisible'
        },
        {
          cellType: 'Empty',
          display: 'NotVisible'
        },
        {
          cellType: 'Empty',
          display: 'NotVisible'
        }
      ];
    const partitionByRowCount = splitEvery(4,board)
    
    return (
    <div className="game-container">
      {partitionByRowCount.map(renderRow)}
    </div>
)}