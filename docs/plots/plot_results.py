"""Run from the project root: python docs/plots/plot_results.py
Requires matplotlib. Reads the measured CSV without modifying it.
"""
import csv
from pathlib import Path
import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
from matplotlib.ticker import MaxNLocator

ROOT = Path(__file__).resolve().parents[2]
with (ROOT / 'results/results.csv').open(newline='', encoding='utf-8-sig') as f:
    rows = list(csv.DictReader(f))
ALGORITHMS = ['MergeSort', 'QuickSort', 'Select', 'ClosestPair']
TYPES = ['random', 'sorted', 'reverse', 'duplicates']
COLORS = ['#2563eb', '#d97706', '#15803d', '#9333ea']
assert len(rows) == 96
assert len({(r['algorithm'], r['input_type'], r['n']) for r in rows}) == 96
for r in rows:
    assert 0 < int(r['min_ns']) <= int(r['median_ns']) <= int(r['max_ns'])

plt.rcParams.update({'font.size': 11, 'axes.spines.top': False,
                     'axes.spines.right': False, 'savefig.facecolor': 'white'})
for metric, filename, title, ylabel in [
    ('median_ns', 'time_vs_n.png', 'Execution time vs. input size', 'Median time (ms)'),
    ('max_depth', 'depth_vs_n.png', 'Maximum recursion depth vs. input size', 'Maximum active recursive depth')
]:
    fig, axes = plt.subplots(2, 2, figsize=(13, 9))
    for ax, kind in zip(axes.flat, TYPES):
        for algorithm, color in zip(ALGORITHMS, COLORS):
            data = sorted([r for r in rows if r['algorithm'] == algorithm and r['input_type'] == kind],
                          key=lambda r: int(r['n']))
            x = [int(r['n']) for r in data]
            y = [int(r[metric]) / (1e6 if metric == 'median_ns' else 1) for r in data]
            ax.plot(x, y, marker='o', markersize=5, linewidth=1.8, color=color, label=algorithm)
        ax.set_xscale('log')
        if metric == 'median_ns':
            ax.set_yscale('log')
        else:
            ax.set_ylim(0, 19)
            ax.yaxis.set_major_locator(MaxNLocator(integer=True))
        ax.set_title({'random': 'Random', 'sorted': 'Sorted', 'reverse': 'Reverse-sorted',
                      'duplicates': 'Duplicate-heavy'}[kind], fontweight='bold')
        ax.set_xlabel('Input size n (log scale)')
        ax.set_ylabel(ylabel)
        ax.grid(True, which='major', alpha=0.23)
    handles, labels = axes.flat[0].get_legend_handles_labels()
    fig.legend(handles, labels, loc='upper center', bbox_to_anchor=(0.5, 0.94), ncol=4, frameon=False)
    fig.suptitle(title, fontsize=19, fontweight='bold', y=0.99)
    note = ('5 warmups + 7 measured runs per case; median shown. Both axes use log scales.'
            if metric == 'median_ns' else
            'Depth counts active recursive calls; the root is depth 1. QuickSort iterates over the larger partition.')
    fig.text(0.5, 0.025, note, ha='center', fontsize=10)
    fig.tight_layout(rect=(0, 0.05, 1, 0.9))
    fig.savefig(ROOT / 'docs/plots' / filename, dpi=180)
    plt.close(fig)
print('Created time_vs_n.png and depth_vs_n.png from 96 measured cases.')
